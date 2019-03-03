package recommender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Recommender {

    private Handler handler = null;

    // For a particular user which products to be recommended
    private Map<String, Product> recommendedProducts = new HashMap<String, Product>();

    /**
     * Constructor for the Recommender where the Handler instance is set.
     * @param handlerIn
     */
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

    /**
     * Collaborative filtering to get recommended products for the input user depending on
     * what his purchased products were.
     */
    public void filterCollaboratively() {

        Iterator iterator = handler.getInput( ).entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            // This is the user who has bought something (the input user)
            String mainUser = (String) pair.getKey();
            // This is the list of products the above user has bought (the input products (products bought))
            List<String> mainProductsBought = (ArrayList<String>) pair.getValue();
            // This is the list of users who have purchased the products in the above list (mainProductsBought)
            Set<String> userList = getListOfRelevantUsers(mainProductsBought);
            // Remove self (mainUser) from userList
            userList.remove(mainUser);
            Set<String> productSet = getListOfRelevantProducts(userList, mainUser);
            System.out.println(productSet);
            for (String pr: productSet) {
                Product product = handler.getProductsMap().get(pr);
                // Check sentiment before adding it to recommendations
                if (product.getSentiment() > 0) recommendedProducts.put(mainUser, product);
            }
        }
    }

    /**
     * A helper method used to get relevant set of users for some products.
     * @param mainProductsBoughtIn
     * @return users
     */
    public Set<String> getListOfRelevantUsers(List<String> mainProductsBoughtIn) {

        Set<String> users = new HashSet<String>();

        if (mainProductsBoughtIn != null && !mainProductsBoughtIn.isEmpty()) {
            for (String product: mainProductsBoughtIn) {
                Set<String> userSet = new HashSet<String>(handler.getProductToUserMap().get(product));
                users.addAll(userSet);
                userSet.clear();
            }
        }
        return users;
    }

    /**
     * A helper method used to get relevant set of products for some products.
     * @param userSetIn
     * @return prds
     */
    public Set<String> getListOfRelevantProducts(Set<String> userSetIn, String mainUserIn) {

        Set<String> prds = new HashSet<String>();

        if(userSetIn != null && !userSetIn.isEmpty()) {
            for (String user: userSetIn) {
                Set<String> productSet = new HashSet<String>(handler.getUserToProductMap().get(user));
                // Remove already bought products
                for (String element: handler.getUserToProductMap().get(mainUserIn)) {
                    productSet.remove(element);
                }
                prds.addAll(productSet);
                productSet.clear();
            }
        }
        return prds;
    }
}
