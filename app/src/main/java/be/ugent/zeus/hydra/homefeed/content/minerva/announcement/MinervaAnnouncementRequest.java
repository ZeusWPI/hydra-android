package be.ugent.zeus.hydra.homefeed.content.minerva.announcement;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

/**
 * @author Niko Strijbol
 */
public class MinervaAnnouncementRequest implements HomeFeedRequest {

    private final AnnouncementDao dao;

    public MinervaAnnouncementRequest(Context context) {
        this.dao = new AnnouncementDao(context);
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        return StreamSupport.stream(dao.getUnread().entrySet())
                .map(s -> new MinervaAnnouncementsCard(s.getValue(), s.getKey()));
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.MINERVA_ANNOUNCEMENT;
    }
}