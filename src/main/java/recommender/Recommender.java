package recommender;

public class Recommender {

    private Handler handler = null;

    public Recommender(Handler handlerIn) {

        handler = handlerIn;
    }

    /**
     * Parent method of the recommendation system
     */
    public void start() {

        if (handler != null) {
            filterCollaboratively();
        }
    }

    public void filterCollaboratively() {




    }
}
