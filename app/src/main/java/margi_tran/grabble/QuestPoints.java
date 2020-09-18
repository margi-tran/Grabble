package margi_tran.grabble;

/**
 *
    QuestPoints.java
 *
 *  This is a utility quest for retrieving the total number of points a quest is worth.
 *
 *  @author Margi Tran */

public class QuestPoints {

    private static final int POINT_PER_KM = 30;
    private static final int POINT_PER_WORD = 50;

    public static int getPointsForDistanceQuest(double distance) {
        return (int) distance * POINT_PER_KM;
    }

    public static int getPointsForWordsQuest(int noOfWords) {
        return noOfWords * POINT_PER_WORD;
    }
}
