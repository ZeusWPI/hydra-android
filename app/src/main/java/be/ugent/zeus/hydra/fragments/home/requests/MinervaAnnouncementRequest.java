package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAnnouncementsCard;
import be.ugent.zeus.hydra.models.minerva.Announcement;
import be.ugent.zeus.hydra.models.minerva.Course;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Niko Strijbol
 */
public class MinervaAnnouncementRequest implements Request<List<HomeCard>>, HomeFeedRequest {

    private final AnnouncementDao dao;

    public MinervaAnnouncementRequest(Context context) {
        this.dao = new AnnouncementDao(context);
    }

    @NonNull
    @Override
    public List<HomeCard> performRequest() throws RequestFailureException {
        Map<Course, List<Announcement>> map = dao.getUnread();
        List<HomeCard> cards = new ArrayList<>();

        for (Map.Entry<Course, List<Announcement>> entry : map.entrySet()) {
            cards.add(new MinervaAnnouncementsCard(entry.getValue(), entry.getKey()));
        }

        return cards;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.MINERVA_ANNOUNCEMENT;
    }
}
