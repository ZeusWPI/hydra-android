package be.ugent.zeus.hydra.common.reporting;

/**
 * @author Niko Strijbol
 */
class DummyHolder implements BaseEvents {

    private static class DummyParams implements Params {

        @Override
        public String method() {
            return "null";
        }

        @Override
        public String searchTerm() {
            return "null";
        }

        @Override
        public String contentType() {
            return "null";
        }

        @Override
        public String itemId() {
            return "null";
        }

        @Override
        public String itemName() {
            return "null";
        }

        @Override
        public String itemCategory() {
            return "null";
        }

        @Override
        public String dismissalType() {
            return "null";
        }

        @Override
        public String cardType() {
            return "null";
        }

        @Override
        public String cardIdentifier() {
            return "null";
        }
    }


    @Override
    public Params params() {
        return new DummyParams();
    }

    @Override
    public String login() {
        return "null";
    }

    @Override
    public String search() {
        return "null";
    }

    @Override
    public String selectContent() {
        return "null";
    }

    @Override
    public String share() {
        return "null";
    }

    @Override
    public String tutorialBegin() {
        return "null";
    }

    @Override
    public String tutorialComplete() {
        return "null";
    }

    @Override
    public String viewItem() {
        return "null";
    }

    @Override
    public String cardDismissal() {
        return "null";
    }
}
