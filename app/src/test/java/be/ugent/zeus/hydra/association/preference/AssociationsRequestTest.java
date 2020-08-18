//package be.ugent.zeus.hydra.association.preference;
//
//import java.util.List;
//
//import be.ugent.zeus.hydra.association.Association;
//import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
//import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//
///**
// * @author Niko Strijbol
// */
//@RunWith(RobolectricTestRunner.class)
//public class AssociationsRequestTest extends AbstractJsonRequestTest<List<Association>> {
//
//    @Override
//    protected String getRelativePath() {
//        return "associations.json";
//    }
//
//    @Override
//    protected JsonOkHttpRequest<List<Association>> getRequest() {
//        return new AssociationsRequest(context);
//    }
//}