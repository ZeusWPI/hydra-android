package be.ugent.zeus.hydra.data.network.requests.minerva;

import android.text.TextUtils;

import be.ugent.zeus.hydra.data.models.minerva.Agenda;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import java8.util.Objects;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Attempts to filter duplicates from a list of calendar items.
 *
 * @author Niko Strijbol
 */
public class AgendaDuplicateDetector implements Function<Agenda, Agenda> {

    @Override
    public Agenda apply(Agenda agenda) {

        List<AgendaItem> agendaItems = agenda.getItems();

        // We first categorize the items per course.
        Map<Course, List<AgendaItem>> mapped = StreamSupport.stream(agendaItems)
                .collect(Collectors.groupingBy(AgendaItem::getCourse));

        List<AgendaItem> items = new ArrayList<>();
        for (Map.Entry<Course, List<AgendaItem>> entry: mapped.entrySet()) {
            items.addAll(filterDuplicates(entry.getKey(), entry.getValue()));
        }

        Agenda newAgenda = new Agenda();
        newAgenda.setItems(items);

        return newAgenda;
    }

    /**
     * Filter the duplicates from the agenda item. The agenda items must all be from the same course.
     *
     * @param course The course.
     * @param items The items for that course.
     *
     * @return The new, filtered list.
     */
    private List<AgendaItem> filterDuplicates(Course course, List<AgendaItem> items) {

        List<AgendaItem> finalItems = new ArrayList<>();

        // Group them by start date.
        Map<ZonedDateTime, List<AgendaItem>> perDate = StreamSupport.stream(items)
                .collect(Collectors.groupingBy(AgendaItem::getStartDate));

        // For every start date, we sort them by end date.
        for (List<AgendaItem> list : perDate.values()) {

            // If there is only one item, add it now and we are done.
            if (list.size() == 1) {
                finalItems.add(list.get(0));
                continue;
            }

            // We group every item by end date.
            Map<ZonedDateTime, List<AgendaItem>> perEndDate = StreamSupport.stream(list)
                    .collect(Collectors.groupingBy(AgendaItem::getEndDate));

            for (List<AgendaItem> endList : perEndDate.values()) {
                if (endList.size() == 1) {
                    finalItems.add(endList.get(0));
                    continue;
                }

                // Decide which one we want. There are a few possibilities here.
                // Firstly, we filter all events that have the course name as title. These are events from Oasis.
                List<AgendaItem> noMoreOasis = StreamSupport.stream(endList)
                        .filter(agendaItem -> agendaItem.getTitle() != null && !agendaItem.getTitle().equals(course.getTitle()))
                        .collect(Collectors.toList());

                // If there is one more left, we assume it is the one from Minerva and add it, and we are done.
                if (noMoreOasis.size() == 1) {
                    // We also mark it as merged for clarity.
                    noMoreOasis.get(0).setMerged(true);
                    finalItems.add(noMoreOasis.get(0));
                    continue;
                }

                // If there are more than one left, we try to merge them.
                // Currently, we only try to merge if everything significant is the same, except the location.
                if (noMoreOasis.size() > 1) {
                    finalItems.addAll(mergeLocations(noMoreOasis));
                    continue;
                }

                // If there are none left, we add them all, since we don't know which one you want.
                assert noMoreOasis.isEmpty();
                finalItems.addAll(mergeLocations(endList));
            }
        }

        return finalItems;
    }

    private List<AgendaItem> mergeLocations(List<AgendaItem> items) {
        // Check if they are all the same
        boolean isSame = true;
        AgendaItem last = items.get(0);
        for (AgendaItem item : items.subList(1, items.size())) {
            // We check the title and description. We already know other things, such as the
            // dates are the same.
            if (!TextUtils.equals(item.getTitle(), last.getTitle()) || !TextUtils.equals(item.getContent(), item.getContent())) {
                isSame = false;
                break;
            }
        }

        if (isSame) {
            // Merge them into one, with an adjusted location. We merge into the first one.
            // TODO: better joining

            String[] locations = StreamSupport.stream(items)
                    .map(AgendaItem::getLocation)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toArray(String[]::new);
            last.setMerged(true);
            last.setLocation(TextUtils.join("\n", locations));
            return Collections.singletonList(last);
        } else {
            // Else we just add them all, since they differ in ways we don't support yet.
            return items;
        }
    }
}
