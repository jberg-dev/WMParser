package coffee.berg.wmparser;

import coffee.berg.wmparser.DataModel.DataHolder;
import coffee.berg.wmparser.DataModel.DataPoint;
import coffee.berg.wmparser.Generics.Pair;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by Bergerking on 2017-05-14.
 */
public class TabFactory {

    public TabFactory() {

    }

    public Optional<Pair<Tab, GenericTabController>> manufactureTab(DataHolder datters) {
        Tab tabby = new Tab();

        Parent main = null;
        FXMLLoader fxmlLoader;
        fxmlLoader =  new FXMLLoader(getClass().getClassLoader().getResource("GenericTab.fxml"));

        try {
            main = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (DataPoint dPoint : datters.getDataPoints())
            dPoint.checkVisible();

        tabby.setContent(main);
        datters.calculateTimesGeneric();
        tabby.setId(datters.getName());
        tabby.setText(datters.getName());

        GenericTabController controller = fxmlLoader.getController();
        if(controller == null)
            return Optional.empty();

        controller.setUp(tabby, datters);

        controller.initializeListOfActions();
        controller.initializeRollingLog();
        controller.initializeBarChart();

        return Optional.of(new Pair(tabby, controller));
    }
}