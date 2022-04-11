// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

import java.util.ArrayList;

public class FuzzySearchBuilder extends Thread {

    String inputThread;

    StringBuilder resultCombined = new StringBuilder();

    public FuzzySearchBuilder(String input) {
        inputThread = input;
    }

    @Override
    public void run() {
        String[] resultSplitArray = inputThread.split(" ");
        ArrayList<String> resultSplitList = new ArrayList<String>();
        for (int i = 0; i < resultSplitArray.length; i++) {
            resultSplitList.add(resultSplitArray[i]);
        }

        for (int i = 0; i < resultSplitList.size(); i++) {
            resultCombined.append(".*");
            resultCombined.append(resultSplitList.get(i));
        }

        resultCombined.append(".*");
    }

    public String getResultCombined() {
        return resultCombined.toString();
    }
}
