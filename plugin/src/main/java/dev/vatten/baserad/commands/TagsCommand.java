/*
 *    Copyright 2026 vatten <vatten.dev>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package dev.vatten.baserad.commands;

import dev.vatten.baserad.*;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TagsCommand extends Command {
    private final GenerateCommand generateCommand;
    private final Plugin plugin;

    public TagsCommand(Plugin plugin, Path importDirectory) {
        super("tags", List.of("fancytags"));
        this.generateCommand = new GenerateCommand(plugin, importDirectory);
        this.plugin = plugin;
    }

    // Welcome to command hell
    // TODO: Redo commands into OOP brigadier-style commands, not whatever this thrown together pile of if statements is
    @Override
    public void execute(VattenPlayer player, String[] args) {
        TagStore tagStore = plugin.getTagStore();
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                plugin.getApi().reload();
                player.sendMessage(plugin.getPluginTextFormatter().format(TextFormatter.SUCCESS.format("Tags plugin has been reloaded!")));
                return;
            }
            if(args[0].equalsIgnoreCase("list")) {
                player.sendMessage(plugin.getPluginTextFormatter().format(TextFormatter.INFO.format("Listing all registered tags:")));
                player.sendMessage(plugin.getInterfaces().createTagListInterface(tagStore).asComponent());
                return;
            }
            if(args[0].equalsIgnoreCase("test")) {
                for (String name : tagStore.getTagNames()) {
                    player.sendMessage(Component.text(name + ": ").append(tagStore.getTag(name).asComponent()));
                }
                return;
            }
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("view")) {
                    if(!tagStore.getTagNames().contains(args[1])) {
                        player.sendMessage(TextFormatter.WARN.format("No tag corresponding to the name '" + args[1] + "' was found."));
                        return;
                    }
                    player.sendMessage(plugin.getPluginTextFormatter().format(TextFormatter.INFO.format("Viewing information about the tag '" + args[1] + "'.")));
                    player.sendMessage(plugin.getInterfaces().createTagInterface(args[1]).asComponent());
                    return;
                }
                if(args[0].equalsIgnoreCase("delete")) {
                    if(!tagStore.getTagNames().contains(args[1])) {
                        player.sendMessage(TextFormatter.WARN.format("No tag corresponding to the name '" + args[1] + "' was found."));
                        return;
                    }
                    player.sendMessage(plugin.getInterfaces().createConfirmInterface(Component.textOfChildren(Component.text("delete '" + args[1] + "' ("), TextFormatter.RESET.format(tagStore.getTag(args[1]).asComponent()), Component.text(")")),
                            (audience) -> {
                                tagStore.removeTag(args[1]);
                                player.sendMessage(plugin.getPluginTextFormatter().format(TextFormatter.SUCCESS.format("Tag '" + args[1] + "' was deleted.")));
                            }
                    ).asComponent());
                    return;
                }
            }
            if(args.length == 3) {
                if(args[0].equalsIgnoreCase("rename")) {
                    if(!tagStore.getTagNames().contains(args[1])) {
                        player.sendMessage(TextFormatter.WARN.format("No tag corresponding to the name '" + args[1] + "' was found."));
                        return;
                    }
                    if(tagStore.getTagNames().contains(args[2])) {
                        player.sendMessage(TextFormatter.WARN.format("A tag with the name '" + args[2] + "' already exists."));
                        return;
                    }
                    player.sendMessage(plugin.getInterfaces().createConfirmInterface(Component.textOfChildren(Component.text("rename '" + args[1] + "' ("), TextFormatter.RESET.format(tagStore.getTag(args[1]).asComponent()), Component.text(") to '" + args[2] + "'")),
                            (audience) -> {
                                if (!plugin.getTagStore().renameTag(args[1], args[2])) {
                                    player.sendMessage(TextFormatter.WARN.format("Tags changed before confirmation; nothing was renamed."));
                                    return;
                                }
                                player.sendMessage(plugin.getPluginTextFormatter().format(TextFormatter.SUCCESS.format("Tag '" + args[1] + "' was renamed to '" + args[2] + "'.")));
                            }
                    ).asComponent());
                    return;
                }
            }

            if(args[0].equalsIgnoreCase("generate")) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                generateCommand.execute(player, newArgs);
                return;
            }
        }
        player.sendMessage(TextFormatter.SEVERE.format(Component.text("You're it!")));
    }

    @Override
    public List<String> onTabComplete(VattenPlayer player, String[] args) {
        List<String> completions = new ArrayList<>();
        if(args.length <= 1) {
            completions.addAll(List.of("reload", "list", "view", "rename", "delete"));
            completions.add(generateCommand.getName());
        }
        if(args.length == 2) {
            if(List.of("view", "rename", "delete").contains(args[0].toLowerCase())) {
                completions.addAll(plugin.getTagStore().getTagNames());
            }
        }
        if(args.length > 1) {
            List<String> check = new ArrayList<>();
            check.add(generateCommand.getName());
            if(check.contains(args[0])) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                return generateCommand.onTabComplete(player, newArgs);
            }
        }
        return completions.stream().filter(s -> args.length == 0 || s.startsWith(args[args.length - 1].toLowerCase())).toList();
    }
}
