package logger;

import java.util.HashMap;
import java.util.Map;

public class AnalyzeLogger {

    public static Map<AnalyzeLoggerLevel, Boolean> levelAllowedMap;

    static {
        levelAllowedMap = new HashMap<>();
        levelAllowedMap.put(AnalyzeLoggerLevel.STAGE, true);
        levelAllowedMap.put(AnalyzeLoggerLevel.INFO, true);
        levelAllowedMap.put(AnalyzeLoggerLevel.WARN, true);
        levelAllowedMap.put(AnalyzeLoggerLevel.ERROR, true);
    }

    public void log(AnalyzeLoggerLevel level, String msg) {
        if (levelAllowedMap.get(level)) {
            System.out.println("[" + level.name() + "]: " + msg);
        }
    }

}
