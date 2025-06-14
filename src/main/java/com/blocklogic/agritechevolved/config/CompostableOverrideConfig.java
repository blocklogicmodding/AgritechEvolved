package com.blocklogic.agritechevolved.config;

import com.blocklogic.agritechevolved.util.RegistryHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
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

public class CompostableOverrideConfig {
    private static final Logger MAIN_LOGGER = LogUtils.getLogger();
    private static org.apache.logging.log4j.Logger ERROR_LOGGER = null;
    private static boolean HAS_LOGGED_ERRORS = false;
    private static Path ERROR_LOG_PATH = null;
    private static final String OVERRIDE_FILE_NAME = "compostable_config_overrides.toml";

    private static final Pattern SECTION_PATTERN = Pattern.compile("\\[(\\w+)\\]");
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(.+)");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[\\s*(.*)\\s*\\]");
    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]*)\"");

    private static void setupErrorLogger() {
        ERROR_LOGGER = LogManager.getLogger(CompostableOverrideConfig.class);
    }

    private static synchronized void createLogFileIfNeeded() {
        if (HAS_LOGGED_ERRORS) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String logFileName = "compostable_config_overrides_errors_" + timestamp + ".log";
            ERROR_LOG_PATH = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/compostable_overrides").resolve("compostable_config_logs").resolve(logFileName);

            Files.createDirectories(ERROR_LOG_PATH.getParent());

            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            PatternLayout layout = PatternLayout.newBuilder()
                    .withPattern("%d{yyyy-MM-dd HH:mm:ss} [%p] %m%n")
                    .build();

            FileAppender appender = FileAppender.newBuilder()
                    .setName("CompostableOverrideErrorAppender")
                    .withFileName(ERROR_LOG_PATH.toString())
                    .setLayout(layout)
                    .setConfiguration(config)
                    .build();

            appender.start();
            config.addAppender(appender);

            LoggerConfig loggerConfig = new LoggerConfig("CompostableOverrideErrorLogger",
                    org.apache.logging.log4j.Level.INFO, false);
            loggerConfig.addAppender(appender, org.apache.logging.log4j.Level.INFO, null);

            config.addLogger("CompostableOverrideErrorLogger", loggerConfig);
            context.updateLoggers();

            ERROR_LOGGER = LogManager.getLogger("CompostableOverrideErrorLogger");

            MAIN_LOGGER.info("Created compostable override config error log file: {}", ERROR_LOG_PATH);
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

    public static void loadOverrides(Set<String> compostableItems) {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve("agritechevolved/compostable_overrides");
        Path overridePath = configDir.resolve(OVERRIDE_FILE_NAME);

        setupErrorLogger();

        if (!Files.exists(overridePath)) {
            createDefaultOverrideFile(configDir, overridePath);
        }

        try {
            MAIN_LOGGER.info("Loading compostable overrides from {}", overridePath);

            Map<String, List<String>> sections = parseTomlFile(overridePath);

            int compostableCount = processCompostableEntries(sections.getOrDefault("compostable", Collections.emptyList()), compostableItems);

            MAIN_LOGGER.info("Successfully loaded {} compostable overrides", compostableCount);
        } catch (Exception e) {
            MAIN_LOGGER.error("Failed to load compostable override.toml file: {}", e.getMessage());
            logError("Failed to load compostable override.toml file: {}", e.getMessage());
            logError("The override file will be ignored, but the mod will continue to function");
        }
    }

    private static Map<String, List<String>> parseTomlFile(Path filePath) throws IOException {
        Map<String, List<String>> result = new HashMap<>();
        String currentSection = null;
        List<String> currentItems = null;

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                int commentPos = line.indexOf('#');
                if (commentPos >= 0) {
                    line = line.substring(0, commentPos);
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                Matcher sectionMatcher = SECTION_PATTERN.matcher(line);
                if (sectionMatcher.matches()) {
                    currentSection = sectionMatcher.group(1);
                    currentItems = new ArrayList<>();
                    result.put(currentSection, currentItems);
                    continue;
                }

                Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(line);
                if (keyValueMatcher.matches() && "compostable".equals(currentSection)) {
                    String value = keyValueMatcher.group(2);
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (currentItems != null) {
                        currentItems.add(value);
                    }
                }
            }
        }

        return result;
    }

    private static int processCompostableEntries(List<String> itemIds, Set<String> compostableItems) {
        int count = 0;

        for (String itemId : itemIds) {
            try {
                Item item = RegistryHelper.getItem(itemId);
                if (item == null) {
                    MAIN_LOGGER.warn("Compostable override uses non-existent item: {}", itemId);
                    logWarning("Compostable override uses non-existent item: {}", itemId);
                    continue;
                }

                compostableItems.add(itemId);
                count++;

                MAIN_LOGGER.info("Added compostable override for item: {}", itemId);

            } catch (Exception e) {
                MAIN_LOGGER.error("Error processing compostable override for '{}': {}", itemId, e.getMessage());
                logError("Error processing compostable override for '{}': {}", itemId, e.getMessage());
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

            MAIN_LOGGER.info("Created default compostable override.toml file with examples at {}", overridePath);

        } catch (IOException e) {
            MAIN_LOGGER.error("Failed to create default compostable override.toml file: {}", e.getMessage());
            if (HAS_LOGGED_ERRORS) {
                ERROR_LOGGER.error("Failed to create default compostable override.toml file: {}", e.getMessage());
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
            # Compostable Items Override Configuration
            # This file allows you to add custom compostable items without modifying the core configuration.
            # Any entries here will be ADDED to the existing configurations.
            
            # How to use:
            # 1. Add items to the [compostable] section using the format: item_name = "mod:item_id"
            # 2. Save the file and restart your game
            
            # IMPORTANT: Make sure to verify the exact item IDs from your mods
            # Incorrect IDs will be skipped with a warning message in the log
            # The mod uses resource location format (e.g., "minecraft:wheat" not just "wheat")
            # The easiest way to check IDs is with F3+H enabled (shows tooltip IDs) or via JEI/REI
            
            # Example compostable items (organic materials for the Composter):
            [compostable]
            # clay = "minecraft:clay"
            # custom_crop = "examplemod:corn"
            # organic_waste = "examplemod:organic_matter"
            
            # Notes:
            # - All vanilla organic items are already included by default
            # - Major mod compatibility (Mystical Agriculture, Farmer's Delight, etc.) is automatic
            # - This file is only for adding items from mods that aren't automatically supported
            """;
    }
}