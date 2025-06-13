package com.blocklogic.agritechevolved.config;

import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantablesOverrideConfig {
    private static final Logger MAIN_LOGGER = LogUtils.getLogger();
    private static org.apache.logging.log4j.Logger ERROR_LOGGER = null;
    private static boolean HAS_LOGGED_ERRORS = false;
    private static Path ERROR_LOG_PATH = null;
    private static final String OVERRIDE_FILE_NAME = "plantables_config_overrides.toml";

    private static final Pattern TABLE_PATTERN = Pattern.compile("\\[(\\w+)\\.([\\w]+)\\]");
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(.+)");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[\\s*(.*)\\s*\\]");
    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]*)\"");

    private static final Map<String, Integer> cropLineNumbers = new HashMap<>();
    private static final Map<String, Integer> treeLineNumbers = new HashMap<>();
    private static final Map<String, Integer> soilLineNumbers = new HashMap<>();

    private static void setupErrorLogger() {
        ERROR_LOGGER = LogManager.getLogger(PlantablesOverrideConfig.class);
    }

    private static synchronized void createLogFileIfNeeded() {
        if (HAS_LOGGED_ERRORS) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String logFileName = "plantables_config_overrides_errors_" + timestamp + ".log";
            ERROR_LOG_PATH = FMLPaths.CONFIGDIR.get().resolve("plantables").resolve("config_logs").resolve(logFileName);

            Files.createDirectories(ERROR_LOG_PATH.getParent());

            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            LoggerConfig existingLogger = config.getLoggerConfig("PlantablesOverrideErrorLogger");
            if (existingLogger != null) {
                existingLogger.getAppenders().forEach((name, appender) -> {
                    existingLogger.removeAppender(name);
                    appender.stop();
                });
                config.removeLogger("PlantablesOverrideErrorLogger");
            }

            PatternLayout layout = PatternLayout.newBuilder()
                    .withPattern("%d{yyyy-MM-dd HH:mm:ss} [%p] %m%n")
                    .build();

            FileAppender appender = FileAppender.newBuilder()
                    .setName("PlantablesOverrideErrorAppender")
                    .withFileName(ERROR_LOG_PATH.toString())
                    .setLayout(layout)
                    .setConfiguration(config)
                    .build();

            appender.start();

            config.addAppender(appender);

            LoggerConfig loggerConfig = new LoggerConfig("PlantablesOverrideErrorLogger",
                    org.apache.logging.log4j.Level.INFO, false);
            loggerConfig.addAppender(appender, org.apache.logging.log4j.Level.INFO, null);

            config.addLogger("PlantablesOverrideErrorLogger", loggerConfig);
            context.updateLoggers();

            ERROR_LOGGER = LogManager.getLogger("PlantablesOverrideErrorLogger");

            MAIN_LOGGER.info("Created override config error log file: {}", ERROR_LOG_PATH);
            HAS_LOGGED_ERRORS = true;
        } catch (Exception e) {
            MAIN_LOGGER.error("Failed to set up dedicated error logger: {}", e.getMessage());
        }
    }

    private static void logError(String message, Object... params) {
        createLogFileIfNeeded();
        ERROR_LOGGER.error(message, params);
    }

    private static void logWarning(String message, Object... params) {
        createLogFileIfNeeded();
        ERROR_LOGGER.warn(message, params);
    }

    public static void loadOverrides(Map<String, PlantablesConfig.CropInfo> crops,
                                     Map<String, PlantablesConfig.TreeInfo> trees,
                                     Map<String, PlantablesConfig.SoilInfo> cropSoils,
                                     Map<String, PlantablesConfig.SoilInfo> treeSoils,
                                     Map<String, PlantablesConfig.FertilizerInfo> fertilizers) {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve("plantables");
        Path overridePath = configDir.resolve(OVERRIDE_FILE_NAME);

        setupErrorLogger();

        if (!Files.exists(overridePath)) {
            createDefaultOverrideFile(configDir, overridePath);
        }

        try {
            MAIN_LOGGER.info("Loading plantables overrides from {}", overridePath);

            cropLineNumbers.clear();
            treeLineNumbers.clear();
            soilLineNumbers.clear();

            Map<String, Map<String, Map<String, Object>>> tables = parseTomlFile(overridePath);

            int cropCount = processCropEntries(tables.getOrDefault("crops", Collections.emptyMap()), crops);
            int treeCount = processTreeEntries(tables.getOrDefault("trees", Collections.emptyMap()), trees);
            int soilCount = processSoilEntries(tables.getOrDefault("soils", Collections.emptyMap()), cropSoils, treeSoils);
            int fertilizerCount = processFertilizerEntries(tables.getOrDefault("fertilizers", Collections.emptyMap()), fertilizers);

            MAIN_LOGGER.info("Successfully loaded {} crop overrides, {} tree overrides, {} soil overrides and {} fertilizer overrides",
                    cropCount, treeCount, soilCount);
        } catch (Exception e) {
            MAIN_LOGGER.error("Failed to load override.toml file: {}", e.getMessage());
            logError("Failed to load override.toml file: {}", e.getMessage());
            logError("The override file will be ignored, but the mod will continue to function");
        }
    }

    private static Map<String, Map<String, Map<String, Object>>> parseTomlFile(Path filePath) throws IOException {
        Map<String, Map<String, Map<String, Object>>> result = new HashMap<>();

        String currentSection = null;
        String currentTable = null;
        Map<String, Map<String, Object>> currentSectionMap = null;
        Map<String, Object> currentTableMap = null;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            StringBuilder multilineValue = null;
            String pendingKey = null;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                int commentPos = findUnquotedChar(line, '#');
                if (commentPos >= 0) {
                    line = line.substring(0, commentPos);
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (multilineValue != null) {
                    multilineValue.append(line);
                    if (countOccurrences(multilineValue.toString(), '[') == countOccurrences(multilineValue.toString(), ']') &&
                            countOccurrences(multilineValue.toString(), '{') == countOccurrences(multilineValue.toString(), '}')) {
                        currentTableMap.put(pendingKey, parseValue(multilineValue.toString()));
                        multilineValue = null;
                        pendingKey = null;
                    }
                    continue;
                }

                Matcher tableMatcher = TABLE_PATTERN.matcher(line);
                if (tableMatcher.matches()) {
                    currentSection = tableMatcher.group(1);
                    currentTable = tableMatcher.group(2);

                    switch (currentSection) {
                        case "crops" -> cropLineNumbers.put(currentTable, lineNumber);
                        case "trees" -> treeLineNumbers.put(currentTable, lineNumber);
                        case "soils" -> soilLineNumbers.put(currentTable, lineNumber);
                    }

                    currentSectionMap = result.computeIfAbsent(currentSection, k -> new HashMap<>());
                    currentTableMap = currentSectionMap.computeIfAbsent(currentTable, k -> new HashMap<>());
                    continue;
                }

                if (currentTableMap != null) {
                    Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(line);
                    if (keyValueMatcher.matches()) {
                        String key = keyValueMatcher.group(1);
                        String valueStr = keyValueMatcher.group(2).trim();

                        if ((valueStr.startsWith("[") && !valueStr.endsWith("]")) ||
                                countOccurrences(valueStr, '[') != countOccurrences(valueStr, ']') ||
                                countOccurrences(valueStr, '{') != countOccurrences(valueStr, '}')) {
                            multilineValue = new StringBuilder(valueStr);
                            pendingKey = key;
                        } else {
                            Object value = parseValue(valueStr);
                            currentTableMap.put(key, value);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static int findUnquotedChar(String str, char target) {
        boolean inQuotes = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == target && !inQuotes) {
                return i;
            }
        }
        return -1;
    }

    private static int countOccurrences(String str, char target) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }

    private static Object parseValue(String valueStr) {
        if (valueStr.startsWith("[") && valueStr.endsWith("]") && valueStr.contains("{")) {
            List<Map<String, Object>> items = new ArrayList<>();

            String content = valueStr.substring(1, valueStr.length() - 1).trim();

            int startIdx = 0;
            while (startIdx < content.length()) {
                int openBrace = content.indexOf('{', startIdx);
                if (openBrace == -1) break;

                int closeBrace = findMatchingCloseBrace(content, openBrace);
                if (closeBrace == -1) break;

                String objectStr = content.substring(openBrace + 1, closeBrace).trim();
                Map<String, Object> objectMap = parseObject(objectStr);
                items.add(objectMap);

                startIdx = closeBrace + 1;
            }

            return items;
        }

        if (valueStr.startsWith("[") && valueStr.endsWith("]")) {
            Matcher arrayMatcher = ARRAY_PATTERN.matcher(valueStr);
            if (arrayMatcher.matches()) {
                String arrayContent = arrayMatcher.group(1);
                List<String> items = new ArrayList<>();

                Matcher stringMatcher = STRING_PATTERN.matcher(arrayContent);
                while (stringMatcher.find()) {
                    items.add(stringMatcher.group(1));
                }

                return items;
            }
        }

        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            return valueStr.substring(1, valueStr.length() - 1);
        }

        try {
            if (valueStr.contains(".")) {
                return Double.parseDouble(valueStr);
            } else {
                return Integer.parseInt(valueStr);
            }
        } catch (NumberFormatException ignored) {
        }

        if (valueStr.equalsIgnoreCase("true")) {
            return true;
        } else if (valueStr.equalsIgnoreCase("false")) {
            return false;
        }

        return valueStr;
    }

    private static int findMatchingCloseBrace(String content, int openBracePos) {
        int depth = 0;
        for (int i = openBracePos; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static Map<String, Object> parseObject(String objectStr) {
        Map<String, Object> result = new HashMap<>();
        String[] parts = objectStr.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            String[] keyValue = part.split("=", 2);
            if (keyValue.length != 2) continue;

            String key = keyValue[0].trim();
            String valueStr = keyValue[1].trim();

            Object value;
            if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
                value = valueStr.substring(1, valueStr.length() - 1);
            } else if (valueStr.equals("true")) {
                value = true;
            } else if (valueStr.equals("false")) {
                value = false;
            } else {
                try {
                    if (valueStr.contains(".")) {
                        value = Double.parseDouble(valueStr);
                    } else {
                        value = Integer.parseInt(valueStr);
                    }
                } catch (NumberFormatException e) {
                    value = valueStr;
                }
            }

            result.put(key, value);
        }

        return result;
    }

    private static int processCropEntries(Map<String, Map<String, Object>> cropEntries,
                                          Map<String, PlantablesConfig.CropInfo> crops) {
        return processPlantEntries(cropEntries, crops, null, cropLineNumbers, "Crop", "seed");
    }

    private static int processTreeEntries(Map<String, Map<String, Object>> treeEntries,
                                          Map<String, PlantablesConfig.TreeInfo> trees) {
        return processPlantEntries(treeEntries, null, trees, treeLineNumbers, "Tree", "sapling");
    }

    @SuppressWarnings("unchecked")
    private static <T> int processPlantEntries(Map<String, Map<String, Object>> plantEntries,
                                               Map<String, PlantablesConfig.CropInfo> crops,
                                               Map<String, PlantablesConfig.TreeInfo> trees,
                                               Map<String, Integer> lineNumbers,
                                               String plantType,
                                               String seedKey) {
        int count = 0;
        boolean isCrop = crops != null;

        for (Map.Entry<String, Map<String, Object>> entry : plantEntries.entrySet()) {
            String plantName = entry.getKey();
            Map<String, Object> plantConfig = entry.getValue();

            try {
                Object seedObj = plantConfig.get(seedKey);
                if (seedObj == null) {
                    int lineNum = lineNumbers.getOrDefault(plantName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("{} override '{}'{} is missing a {} ID, skipping", plantType, plantName, lineInfo, seedKey);
                    logWarning("{} override '{}'{} is missing a {} ID, skipping", plantType, plantName, lineInfo, seedKey);
                    continue;
                }

                String seedId = seedObj.toString();

                Item seedItem = RegistryHelper.getItem(seedId);
                if (seedItem == null) {
                    int lineNum = lineNumbers.getOrDefault(plantName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("{} override '{}'{} uses non-existent {} item: {}, skipping", plantType, plantName, lineInfo, seedKey, seedId);
                    logWarning("{} override '{}'{} uses non-existent {} item: {}, skipping", plantType, plantName, lineInfo, seedKey, seedId);
                    continue;
                }

                List<String> validSoils = new ArrayList<>();
                Object soilsObj = plantConfig.get("soil");
                if (soilsObj instanceof List<?> soilsList) {
                    for (Object soilObj : soilsList) {
                        String soilId = soilObj.toString();
                        Block soilBlock = RegistryHelper.getBlock(soilId);
                        if (soilBlock == null) {
                            int lineNum = lineNumbers.getOrDefault(plantName, -1);
                            String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                            MAIN_LOGGER.warn("{} override '{}'{} references non-existent soil block: {}, skipping this soil",
                                    plantType, plantName, lineInfo, soilId);
                            logWarning("{} override '{}'{} references non-existent soil block: {}, skipping this soil",
                                    plantType, plantName, lineInfo, soilId);
                            continue;
                        }
                        validSoils.add(soilId);
                    }
                }

                if (validSoils.isEmpty()) {
                    int lineNum = lineNumbers.getOrDefault(plantName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("{} override '{}'{} has no valid soils, skipping", plantType, plantName, lineInfo);
                    logWarning("{} override '{}'{} has no valid soils, skipping", plantType, plantName, lineInfo);
                    continue;
                }

                List<?> drops = processDrops(plantConfig, plantName, lineNumbers, plantType, isCrop);
                if (drops.isEmpty()) {
                    int lineNum = lineNumbers.getOrDefault(plantName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("{} override '{}'{} has no valid drops, skipping", plantType, plantName, lineInfo);
                    logWarning("{} override '{}'{} has no valid drops, skipping", plantType, plantName, lineInfo);
                    continue;
                }

                if (isCrop) {
                    PlantablesConfig.CropInfo cropInfo = new PlantablesConfig.CropInfo();
                    cropInfo.validSoils = validSoils;
                    cropInfo.drops = (List<PlantablesConfig.DropInfo>) drops;
                    crops.put(seedId, cropInfo);
                } else {
                    PlantablesConfig.TreeInfo treeInfo = new PlantablesConfig.TreeInfo();
                    treeInfo.validSoils = validSoils;
                    treeInfo.drops = (List<PlantablesConfig.DropInfo>) drops;
                    trees.put(seedId, treeInfo);
                }

                count++;
                MAIN_LOGGER.info("Added {} override for '{}' with {} {}", plantType.toLowerCase(), plantName, seedKey, seedId);

            } catch (Exception e) {
                int lineNum = lineNumbers.getOrDefault(plantName, -1);
                String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                MAIN_LOGGER.error("Error processing {} override '{}'{}: {}", plantType.toLowerCase(), plantName, lineInfo, e.getMessage());
                logError("Error processing {} override '{}'{}: {}", plantType.toLowerCase(), plantName, lineInfo, e.getMessage());
            }
        }

        return count;
    }

    private static List<?> processDrops(Map<String, Object> plantConfig, String plantName,
                                        Map<String, Integer> lineNumbers, String plantType, boolean isCrop) {
        Object dropsObj = plantConfig.get("drops");
        if (!(dropsObj instanceof List<?> dropsList)) {
            return Collections.emptyList();
        }

        List<Object> drops = new ArrayList<>();

        int defaultMinCount = 1;
        int defaultMaxCount = 1;
        float defaultChance = 1.0f;

        if (plantConfig.containsKey("min_count") && plantConfig.get("min_count") instanceof Number) {
            defaultMinCount = ((Number) plantConfig.get("min_count")).intValue();
        }
        if (plantConfig.containsKey("max_count") && plantConfig.get("max_count") instanceof Number) {
            defaultMaxCount = ((Number) plantConfig.get("max_count")).intValue();
        }
        if (plantConfig.containsKey("chance") && plantConfig.get("chance") instanceof Number) {
            defaultChance = ((Number) plantConfig.get("chance")).floatValue();
        }

        for (Object dropObj : dropsList) {
            String dropId;
            int minCount = defaultMinCount;
            int maxCount = defaultMaxCount;
            float chance = defaultChance;

            if (dropObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> dropMap = (Map<String, Object>) dropObj;

                Object itemObj = dropMap.get("item");
                if (itemObj == null) {
                    int lineNum = lineNumbers.getOrDefault(plantName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("{} override '{}'{} has drop without item ID, skipping", plantType, plantName, lineInfo);
                    logWarning("{} override '{}'{} has drop without item ID, skipping", plantType, plantName, lineInfo);
                    continue;
                }

                dropId = itemObj.toString();

                if (dropMap.containsKey("min_count") && dropMap.get("min_count") instanceof Number) {
                    minCount = ((Number) dropMap.get("min_count")).intValue();
                }
                if (dropMap.containsKey("max_count") && dropMap.get("max_count") instanceof Number) {
                    maxCount = ((Number) dropMap.get("max_count")).intValue();
                }
                if (dropMap.containsKey("chance") && dropMap.get("chance") instanceof Number) {
                    chance = ((Number) dropMap.get("chance")).floatValue();
                }
            } else {
                dropId = dropObj.toString();
            }

            Item dropItem = RegistryHelper.getItem(dropId);
            if (dropItem == null) {
                int lineNum = lineNumbers.getOrDefault(plantName, -1);
                String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                MAIN_LOGGER.warn("{} override '{}'{} references non-existent drop item: {}, skipping this drop",
                        plantType, plantName, lineInfo, dropId);
                logWarning("{} override '{}'{} references non-existent drop item: {}, skipping this drop",
                        plantType, plantName, lineInfo, dropId);
                continue;
            }

            if (isCrop) {
                drops.add(new PlantablesConfig.DropInfo(dropId, minCount, maxCount, chance));
            } else {
                drops.add(new PlantablesConfig.DropInfo(dropId, minCount, maxCount, chance));
            }
        }

        return drops;
    }

    private static int processSoilEntries(Map<String, Map<String, Object>> soilEntries,
                                          Map<String, PlantablesConfig.SoilInfo> cropSoils,
                                          Map<String, PlantablesConfig.SoilInfo> treeSoils) {
        int count = 0;

        for (Map.Entry<String, Map<String, Object>> entry : soilEntries.entrySet()) {
            String soilName = entry.getKey();
            Map<String, Object> soilConfig = entry.getValue();

            try {
                Object blockObj = soilConfig.get("block");
                if (blockObj == null) {
                    int lineNum = soilLineNumbers.getOrDefault(soilName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("Soil override '{}'{} is missing a block ID, skipping", soilName, lineInfo);
                    logWarning("Soil override '{}'{} is missing a block ID, skipping", soilName, lineInfo);
                    continue;
                }

                String soilId = blockObj.toString();

                Block soilBlock = RegistryHelper.getBlock(soilId);
                if (soilBlock == null) {
                    int lineNum = soilLineNumbers.getOrDefault(soilName, -1);
                    String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                    MAIN_LOGGER.warn("Soil override '{}'{} uses non-existent block: {}, skipping", soilName, lineInfo, soilId);
                    logWarning("Soil override '{}'{} uses non-existent block: {}, skipping", soilName, lineInfo, soilId);
                    continue;
                }

                float growthModifier = 1.0f;
                Object modifierObj = soilConfig.get("growth_modifier");
                if (modifierObj instanceof Number) {
                    growthModifier = ((Number) modifierObj).floatValue();
                }

                PlantablesConfig.SoilInfo cropSoilInfo = new PlantablesConfig.SoilInfo(growthModifier < 1.0f ? growthModifier : 0.5f);
                PlantablesConfig.SoilInfo treeSoilInfo = new PlantablesConfig.SoilInfo(growthModifier);

                cropSoils.put(soilId, cropSoilInfo);
                treeSoils.put(soilId, treeSoilInfo);
                count++;

                MAIN_LOGGER.info("Added soil override for '{}' with block {}", soilName, soilId);

            } catch (Exception e) {
                int lineNum = soilLineNumbers.getOrDefault(soilName, -1);
                String lineInfo = lineNum > 0 ? " (line " + lineNum + ")" : "";
                MAIN_LOGGER.error("Error processing soil override '{}'{}: {}", soilName, lineInfo, e.getMessage());
                logError("Error processing soil override '{}'{}: {}", soilName, lineInfo, e.getMessage());
            }
        }

        return count;
    }

    private static int processFertilizerEntries(Map<String, Map<String, Object>> fertilizerEntries,
                                                Map<String, PlantablesConfig.FertilizerInfo> fertilizers) {
        int count = 0;

        for (Map.Entry<String, Map<String, Object>> entry : fertilizerEntries.entrySet()) {
            String fertilizerName = entry.getKey();
            Map<String, Object> fertilizerConfig = entry.getValue();

            try {
                Object itemObj = fertilizerConfig.get("item");
                if (itemObj == null) {
                    MAIN_LOGGER.warn("Fertilizer override '{}' is missing item ID", fertilizerName);
                    continue;
                }

                String itemId = itemObj.toString();
                Item item = RegistryHelper.getItem(itemId);
                if (item == null) {
                    MAIN_LOGGER.warn("Fertilizer override '{}' uses non-existent item: {}", fertilizerName, itemId);
                    continue;
                }

                float speedMultiplier = 1.2f;
                float yieldMultiplier = 1.2f;

                if (fertilizerConfig.containsKey("speed_multiplier") && fertilizerConfig.get("speed_multiplier") instanceof Number) {
                    speedMultiplier = ((Number) fertilizerConfig.get("speed_multiplier")).floatValue();
                }

                if (fertilizerConfig.containsKey("yield_multiplier") && fertilizerConfig.get("yield_multiplier") instanceof Number) {
                    yieldMultiplier = ((Number) fertilizerConfig.get("yield_multiplier")).floatValue();
                }

                fertilizers.put(itemId, new PlantablesConfig.FertilizerInfo(speedMultiplier, yieldMultiplier));
                count++;

                MAIN_LOGGER.info("Added fertilizer override for '{}' with item {}", fertilizerName, itemId);

            } catch (Exception e) {
                MAIN_LOGGER.error("Error processing fertilizer override '{}': {}", fertilizerName, e.getMessage());
            }
        }

        return count;
    }

    private static void createDefaultOverrideFile(Path configDir, Path overridePath) {
        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            try (FileWriter writer = new FileWriter(overridePath.toFile())) {
                writer.write(createBasicTemplate());
            }

            MAIN_LOGGER.info("Created default override.toml file with examples at {}", overridePath);

        } catch (IOException e) {
            MAIN_LOGGER.error("Failed to create default override.toml file: {}", e.getMessage());
            if (HAS_LOGGED_ERRORS) {
                ERROR_LOGGER.error("Failed to create default override.toml file: {}", e.getMessage());
            }
        }
    }

    public static void resetErrorFlag() {
        HAS_LOGGED_ERRORS = false;
        ERROR_LOGGER = null;
        ERROR_LOG_PATH = null;
    }

    private static String createBasicTemplate() {
        return """
                # Plantables Override Configuration
                # This file allows you to add custom crops, trees, and soils without modifying the core configuration.
                # Any entries here will override existing configurations for the same items/blocks.
                
                # How to use:
                # 1. Add [crops.your_crop_name] sections for new crops or to override existing ones
                # 2. Add [trees.your_tree_name] sections for new trees or to override existing ones
                # 3. Add [soils.your_soil_name] sections for new soils or to override existing ones
                # 4. Save the file and restart your game
                
                # IMPORTANT: Make sure to verify the exact item and block IDs from your mods
                # Incorrect IDs will be skipped with a warning message in the log
                # The mod uses resource location format (e.g., "minecraft:dirt" not just "dirt")
                # The easiest way to check IDs is with F3+H enabled (shows tooltip IDs) or via JEI/REI
                
                # Example crops:
                
                # [crops.example_wheat]
                # seed = "examplemod:wheat_seeds"
                # soil = [
                #   "minecraft:farmland",
                #   "examplemod:rich_soil"
                # ]
                # # There are two ways to specify drops:
                # # 1. Simple drops with default settings:
                # drops = [
                #   "examplemod:wheat",
                #   "examplemod:wheat_seeds"
                # ]
                # min_count = 1  # Minimum drop count for all simple drops (default: 1)
                # max_count = 3  # Maximum drop count for all simple drops (default: 1)
                # chance = 0.75  # Drop chance for all simple drops (default: 1.0)
                
                # # 2. Detailed drops with individual settings (use this for multiple drops with different chances):
                # # [crops.example_wheat_advanced]
                # # seed = "examplemod:wheat_seeds"
                # # soil = ["minecraft:farmland"]
                # # drops = [
                # #   { item = "examplemod:wheat", min_count = 1, max_count = 3, chance = 1.0 },
                # #   { item = "examplemod:wheat_seeds", min_count = 1, max_count = 2, chance = 0.3 }
                # # ]
                
                # Example trees:
                
                # [trees.example_oak]
                # sapling = "examplemod:oak_sapling"
                # soil = [
                #   "minecraft:dirt",
                #   "examplemod:rich_soil"
                # ]
                # # There are two ways to specify drops:
                # # 1. Simple drops with default settings:
                # drops = [
                #   "examplemod:oak_log",
                #   "examplemod:oak_sapling"
                # ]
                # min_count = 1  # Minimum drop count for all simple drops (default: 1)
                # max_count = 3  # Maximum drop count for all simple drops (default: 1)
                # chance = 0.75  # Drop chance for all simple drops (default: 1.0)
                
                # # 2. Detailed drops with individual settings (use this for multiple drops with different chances):
                # # [trees.example_oak_advanced]
                # # sapling = "examplemod:oak_sapling"
                # # soil = ["minecraft:dirt"]
                # # drops = [
                # #   { item = "examplemod:oak_log", min_count = 2, max_count = 6, chance = 1.0 },
                # #   { item = "examplemod:oak_sapling", min_count = 1, max_count = 2, chance = 0.5 },
                # #   { item = "minecraft:apple", min_count = 1, max_count = 2, chance = 0.2 }
                # # ]
                
                # Example soils:
                
                # [soils.example_rich_soil]
                # block = "examplemod:rich_soil"
                # growth_modifier = 1.5  # Growth speed multiplier (default: 1.0)
                
                # REAL EXAMPLES - Uncomment to use
                
                # This example adds support for a mod's herb crop
                # [crops.herb_crop]
                # seed = "herbmod:herb_seeds"
                # soil = [
                #   "minecraft:farmland",
                #   "minecraft:dirt",
                #   "minecraft:clay"
                # ]
                # drops = [
                #   { item = "herbmod:herb", min_count = 1, max_count = 3, chance = 1.0 },
                #   { item = "herbmod:herb_seeds", min_count = 1, max_count = 2, chance = 0.3 }
                # ]
                
                # This example adds support for a mod's magical tree
                # [trees.magical_tree]
                # sapling = "awesomemod:magical_sapling"
                # soil = [
                #   "minecraft:dirt",
                #   "minecraft:grass_block",
                #   "minecraft:podzol"
                # ]
                # drops = [
                #   { item = "awesomemod:magical_log", min_count = 3, max_count = 7, chance = 1.0 },
                #   { item = "awesomemod:magical_sapling", min_count = 1, max_count = 2, chance = 0.5 },
                #   { item = "awesomemod:magical_fruit", min_count = 1, max_count = 3, chance = 0.4 }
                # ]
                
                # This example adds enriched soil for both crops and trees
                # [soils.enriched_soil]
                # block = "herbmod:enriched_soil"
                # growth_modifier = 1.25
                
                # Example fertilizers:
          
                # [fertilizers.bone_meal]
                # item = "minecraft:bone_meal"
                # speed_multiplier = 1.2  # 20% faster growth
                # yield_multiplier = 1.2  # 20% more drops
                                
                # [fertilizers.biomass]
                # item = "agritechevolved:biomass"
                # speed_multiplier = 1.2
                # yield_multiplier = 1.2
                                
                # [fertilizers.mystical_fertilizer]
                # item = "mysticalagriculture:mystical_fertilizer"
                # speed_multiplier = 1.2
                # yield_multiplier = 1.2
                """;
    }
}