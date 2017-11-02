package be.ugent.zeus.hydra.data.network.requests.minerva;

import android.text.TextUtils;

import be.ugent.zeus.hydra.data.models.minerva.Agenda;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import java8.lang.Iterables;
import java8.util.Objects;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.util.*;

/**
 * Attempts to filter duplicates from a list of calendar items.
 *
 * @author Niko Strijbol
 */
public class AgendaDuplicateDetector implements Function<Agenda, Agenda> {

    /**
     * These are edit modes we do not want to show to the user.
     */
    private static final Set<String> HIDDEN_TYPES = new HashSet<>(Collections.singletonList("set_invisible"));

    @Override
    public Agenda apply(Agenda agenda) {

        List<AgendaItem> agendaItems = agenda.getItems();

        Iterables.removeIf(agendaItems, item -> HIDDEN_TYPES.contains(item.getLastEditType()));

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
                finalItems.addAll(mergeLocations(endList));
            }
        }

        return finalItems;
    }

    private List<AgendaItem> mergeLocations(List<AgendaItem> items) {

        List<AgendaItem> finalItems = new ArrayList<>();

        // We currently consider two events the same if their titles are the same. Group the events by title.
        Map<String, List<AgendaItem>> perTitle = StreamSupport.stream(items)
                .collect(Collectors.groupingBy(AgendaItem::getTitle));

        for (List<AgendaItem> item : perTitle.values()) {
            if (item.size() == 1) {
                finalItems.add(item.get(0));
                continue;
            }
            // Get the first one.
            AgendaItem first = items.get(0);
            // Merge the locations.
            String[] locations = StreamSupport.stream(item)
                    .map(AgendaItem::getLocation)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toArray(String[]::new);
            first.setLocation(TextUtils.join("\n", locations));
            first.setMerged(true);
            finalItems.add(first);
        }

        return finalItems;
    }
}
