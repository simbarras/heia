/*package ch.heiafr.code_robuste;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.general_libraries.results.DataPoint;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class TechDebtExperiment implements Experiment {
    private final int NB_RUNS = 10;
    private double sales;
    private double techDebt;
    private double devEffort;
    private double devTechRatio;


    public TechDebtExperiment(
            @ParamName(name = "sales", default_ = "10000") double sales,
            @ParamName(name = "Dette technique", default_ = "1") double techDebt,
            @ParamName(name = "Effort de developpement", default_ = "1") double devEffort,
            @ParamName(name = "Ratio de developpement technique", default_ = "1") double devTechRatio) {
        this.sales = sales;
        this.techDebt = techDebt;
        this.devEffort = devEffort;
        this.devTechRatio = devTechRatio;
    }


    @Override
    public void run(AbstractResultsManager resultMan, AbstractResultsDisplayer dis) throws WrongExperimentException, IOException {
        // valeurs pour l'exemple
        DataPoint dp = new DataPoint();
        for (int i = 0; i < NB_RUNS; i++) {
            URL url = new URL("http://test.rumley.pro:8080/api1?sales=" + sales + "&techDebt=" + techDebt + ".2&devEffort=" + devEffort + "&devTechDeptRatio=" + devTechRatio);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            JSONObject json = new JSONObject(content.toString());
            double nextSales = json.getDouble("nextSales");
            double nextTechDebt = json.getDouble("nextTechDebt");
            double devCost = json.getDouble("devCost");


            if (i == 0) {
                dp = new DataPoint();
            } else {
                dp = dp.getDerivedDataPoint();
            }
            dp.addProperty("sales", this.sales);
            dp.addProperty("techDebt", this.techDebt);
            dp.addProperty("devEffort", this.devEffort);
            dp.addProperty("devTechRatio", this.devTechRatio);
            dp.addResultProperty("nextSales", nextSales);
            dp.addResultProperty("nextTechDebt", nextTechDebt);
            dp.addResultProperty("devCost", devCost);

            resultMan.addDataPoint(dp);

            this.techDebt = nextTechDebt;
            this.sales = nextSales;

        }
    }
}*/