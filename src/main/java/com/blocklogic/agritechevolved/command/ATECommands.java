package com.blocklogic.agritechevolved.command;

import com.blocklogic.agritechevolved.Config;
import com.blocklogic.agritechevolved.config.PlantablesConfig;
import com.blocklogic.agritechevolved.config.PlantablesOverrideConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ATECommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("agritechevolved")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("reload")
                                .then(Commands.literal("plantables")
                                        .executes(context -> {
                                            try {
                                                PlantablesOverrideConfig.resetErrorFlag();
                                                PlantablesConfig.loadConfig();
                                                context.getSource().sendSuccess(() ->
                                                        Component.literal("AgriTech: Evolved plantables config reloaded successfully!"), true);
                                                return 1;
                                            } catch (Exception e) {
                                                context.getSource().sendFailure(
                                                        Component.literal("Failed to reload AgriTech: Evolved plantables config: " + e.getMessage()));
                                                return 0;
                                            }
                                        })
                                )
                                .then(Commands.literal("config")
                                        .executes(context -> {
                                            try {
                                                Config.loadConfig();
                                                context.getSource().sendSuccess(() ->
                                                        Component.literal("AgriTech: Evolved main config reloaded successfully!"), true);
                                                return 1;
                                            } catch (Exception e) {
                                                context.getSource().sendFailure(
                                                        Component.literal("Failed to reload AgriTech: Evolved main config: " + e.getMessage()));
                                                return 0;
                                            }
                                        })
                                )
                                .executes(context -> {
                                    try {
                                        PlantablesOverrideConfig.resetErrorFlag();
                                        Config.loadConfig();
                                        PlantablesConfig.loadConfig();
                                        context.getSource().sendSuccess(() ->
                                                Component.literal("All AgriTech: Evolved configs reloaded successfully!"), true);
                                        return 1;
                                    } catch (Exception e) {
                                        context.getSource().sendFailure(
                                                Component.literal("Failed to reload AgriTech: Evolved configs: " + e.getMessage()));
                                        return 0;
                                    }
                                })
                        )
        );
    }
}
