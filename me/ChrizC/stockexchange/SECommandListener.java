package me.ChrizC.stockexchange;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class SECommandListener {
    
    private final StockExchange plugin;
    SEMarketHandler marketHandler;
    SEScheduleHandler scheduleHandler;
    SEConfig config;
    SEFileHandler fileHandler;
    
    Player player;
    
    List list = new ArrayList();
    
    public SECommandListener(StockExchange instance, SEMarketHandler marketHandler, SEScheduleHandler scheduleHandler, SEConfig config, SEFileHandler fileHandler) {
        plugin = instance;
        this.marketHandler = marketHandler;
        this.scheduleHandler = scheduleHandler;
        this.config = config;
        this.fileHandler = fileHandler;
    }
    
    public void setupCommands() {
        PluginCommand stocks = plugin.getCommand("stocks");
        CommandExecutor commandExecutor = new CommandExecutor() {
            public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
                if (sender instanceof Player) {
                    if (args.length > 0) {
                        stocks(sender, args);
                    }
                } else {
                    consolestocks(sender, args);
                }
                
                  
                return true;
            }
        };
        if (stocks != null) {
            stocks.setExecutor(commandExecutor);
        }
    }
    
    public void stocks(CommandSender event, String[] args) {
        player = (Player) event;
        if (args.length >= 1) {
            if (args[0].equals("top5")) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.users.top5")) {
                    marketHandler.top5(event);
                } else if (plugin.permissionHandler == null) {
                    marketHandler.top5(event);
                }
            } else if (args[0].equals("add")) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.admin.add")) {
                    if (args.length == 2) {
                        marketHandler.add(player, args[1], 1.0);
                    } else if (args.length >= 3) {
                        marketHandler.add(player, args[1], Double.parseDouble(args[2]));
                    }
                } else if (plugin.permissionHandler == null && player.isOp()) {
                    if (args.length == 2) {
                        marketHandler.add(player, args[1], 1.0);
                    } else if (args.length >= 3) {
                        marketHandler.add(player, args[1], Double.parseDouble(args[2]));
                    }
                } 
            } else if (args[0].equals("remove") && args.length > 1) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.admin.remove")) {
                    marketHandler.remove(player, args[1]);
                } else if (plugin.permissionHandler == null && player.isOp()) {
                    marketHandler.remove(player, args[1]);
                }
            } else if (args[0].equals("lookup") && args.length > 1) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.users.lookup")) {
                    marketHandler.lookup(player, args[1]);
                } else if (plugin.permissionHandler == null) {
                    marketHandler.lookup(player, args[1]);
                } 
            } else if (args[0].equals("buy") && args.length > 2) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.users.trade")) {
                    if (args[2].equals("max")) {
                        marketHandler.buymax(player, args[1]);
                    } else {
                        marketHandler.buy(player, args[1], Integer.parseInt(args[2]));
                    }
                } else if (plugin.permissionHandler == null) {
                    if (args[2].equals("max")) {
                        marketHandler.buymax(player, args[1]);
                    } else {
                        marketHandler.buy(player, args[1], Integer.parseInt(args[2]));
                    }
                } 
            } else if (args[0].equals("sell") && args.length > 2) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.users.trade")) {
                    if (args[2].equals("all")) {
                        marketHandler.sellall(player, args[1]);
                    } else {
                        marketHandler.sell(player, args[1], Integer.parseInt(args[2]));
                    }
                } else if (plugin.permissionHandler == null) {
                    if (args[2].equals("all")) {
                        marketHandler.sellall(player, args[1]);
                    } else {
                        marketHandler.sell(player, args[1], Integer.parseInt(args[2]));
                    }
                } 
            } else if (args[0].equals("increase") && args.length > 2) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.admin.modify")) {
                    marketHandler.increase(player, args[1], Double.parseDouble(args[2]));
                } else if (plugin.permissionHandler == null && player.isOp()) {
                    marketHandler.increase(player, args[1], Double.parseDouble(args[2]));
                } 
            } else if (args[0].equals("decrease") && args.length > 2) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.admin.modify")) {
                    marketHandler.decrease(player, args[1], Double.parseDouble(args[2]));
                } else if (plugin.permissionHandler == null && player.isOp()) {
                    marketHandler.decrease(player, args[1], Double.parseDouble(args[2]));
                } 
            } else if (args[0].equals("stop")) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.admin.schedule")) {
                    if (scheduleHandler.taskId != 0) {
                        Bukkit.getServer().getScheduler().cancelTask(scheduleHandler.taskId);
                        scheduleHandler.taskId = 0;
                        player.sendMessage(ChatColor.DARK_PURPLE + "[Stocks] Stock fluctuations stopped!");
                    }
                } else if (plugin.permissionHandler == null && player.isOp()) {
                    if (scheduleHandler.taskId != 0) {
                        Bukkit.getServer().getScheduler().cancelTask(scheduleHandler.taskId);
                        scheduleHandler.taskId = 0;
                        player.sendMessage(ChatColor.DARK_PURPLE + "[Stocks] Stock fluctuations stopped!");
                    }
                }
            } else if (args[0].equals("start")) {
                if (plugin.permissionHandler != null && plugin.permissionHandler.has(player, "stocks.admin.schedule")) {
                    if (config.flucsEnabled == true) {
                        if (scheduleHandler.taskId == 0) {
                            scheduleHandler.fluctuate(config.min, config.max, config.delay, config.broadcast);
                            player.sendMessage(ChatColor.DARK_PURPLE + "[Stocks] Stock fluctuations started!");
                        }
                    }
                } else if (plugin.permissionHandler == null && player.isOp()) {
                    if (config.flucsEnabled == true) {
                        if (scheduleHandler.taskId == 0) {
                            scheduleHandler.fluctuate(config.min, config.max, config.delay, config.broadcast);
                            player.sendMessage(ChatColor.DARK_PURPLE + "[Stocks] Stock fluctuations started!");
                        }
                    }
                }
            } else if (args[0].equals("portfolio") || args[0].equals("showmine")) {
                marketHandler.portfolio(player);
            }
        }
    }
    
    public void consolestocks(CommandSender event, String[] args) {
        if (args.length >= 1) {
            if (args[0].equals("top5")) {
                marketHandler.top5(event);
            } else if (args[0].equals("add")) {
                if (args.length == 2) {
                    marketHandler.add(event, args[1], 1.0);
                } else if (args.length >= 3) {
                    marketHandler.add(event, args[1], Double.parseDouble(args[2]));
                }
            } else if (args[0].equals("remove") && args.length > 1) {
                marketHandler.remove(event, args[1]);
            } else if (args[0].equals("lookup") && args.length > 1) {
                marketHandler.lookup(event, args[1]);
            } else if (args[0].equals("increase") && args.length > 2) {
                marketHandler.increase(event, args[1], Double.parseDouble(args[2]));
            } else if (args[0].equals("decrease") && args.length > 2) {
                marketHandler.decrease(event, args[1], Double.parseDouble(args[2]));
            } else if (args[0].equals("stop")) {
                if (scheduleHandler.taskId != 0) {
                    Bukkit.getServer().getScheduler().cancelTask(scheduleHandler.taskId);
                    scheduleHandler.taskId = 0;
                    event.sendMessage(ChatColor.DARK_PURPLE + "[Stocks] Stock fluctuations stopped!");
                }
            } else if (args[0].equals("start")) {
                if (config.flucsEnabled == true) {
                    if (scheduleHandler.taskId == 0) {
                        scheduleHandler.fluctuate(config.min, config.max, config.delay, config.broadcast);
                        event.sendMessage(ChatColor.DARK_PURPLE + "[Stocks] Stock fluctuations started!");
                    }
                }
            } else if (args[0].equals("rollback")) {
                if (args.length == 2) {
                    if (fileHandler.rollback(Integer.parseInt(args[1])) == true) {
                        event.sendMessage("[Stocks] Successfully rolled back to backup #" + args[1]);
                    } else {
                        System.err.println("[Stocks] Error in rolling back to backup #" + args[1] + ". (Does the backup exist?)");
                    }
                }
            } else if (args[0].equals("undo")) {
                if (fileHandler.undo() == true) {
                    event.sendMessage("[Stocks] Successfully undid rollback.");
                } else {
                    System.err.println("[Stocks] Unknown error in undoing rollback!");
                }
            }
        }
    }
}