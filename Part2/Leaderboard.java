import java.util.HashMap;

public class Leaderboard
{
    private static HashMap<String, Integer> points = new HashMap<>();
    private static HashMap<String, Integer> winStreak = new HashMap<>();
    private static HashMap<String, Integer> noBurnoutStreak = new HashMap<>();

    public static void addPoints(String name, int pts)
    {
        points.put(name, points.getOrDefault(name, 0) + pts);
    }

    public static int getPoints(String name)
    {
        return points.getOrDefault(name, 0);
    }

    public static HashMap<String, Integer> getAllPoints()
    {
        return points;
    }

    // Streak tracking
    public static void recordWin(String name)
    {
        winStreak.put(name, winStreak.getOrDefault(name, 0) + 1);
    }

    public static void resetWinStreak(String name)
    {
        winStreak.put(name, 0);
    }

    public static int getWinStreak(String name)
    {
        return winStreak.getOrDefault(name, 0);
    }

    public static void recordNoBurnout(String name)
    {
        noBurnoutStreak.put(name, noBurnoutStreak.getOrDefault(name, 0) + 1);
    }

    public static void resetNoBurnout(String name)
    {
        noBurnoutStreak.put(name, 0);
    }

    public static int getNoBurnoutStreak(String name)
    {
        return noBurnoutStreak.getOrDefault(name, 0);
    }
}