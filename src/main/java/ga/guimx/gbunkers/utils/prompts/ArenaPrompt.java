package ga.guimx.gbunkers.utils.prompts;

import ga.guimx.gbunkers.GBunkers;
import ga.guimx.gbunkers.config.PluginConfig;
import ga.guimx.gbunkers.utils.Arena;
import ga.guimx.gbunkers.utils.Chat;
import org.bukkit.Color;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class ArenaPrompt {
    private Arena arena = Arena.builder()
            .redTeam(Arena.Team.builder().color(Color.RED).build())
            .blueTeam(Arena.Team.builder().color(Color.BLUE).build())
            .yellowTeam(Arena.Team.builder().color(Color.YELLOW).build())
            .greenTeam(Arena.Team.builder().color(Color.GREEN).build())
            .koth(Arena.Koth.builder().build())
            .build();
    public Arena generateSetArenaPrompt(Player player){
        ConversationFactory factory = new ConversationFactory(GBunkers.getInstance());




        Prompt setPearlsDisabled = new FixedSetPrompt("y","n","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getKoth().setArePearlsDisabled(s.equalsIgnoreCase("y"));
                return setRedTeamSellShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_koth_pearls_disabled"));
            }
        };
        Prompt setKothClaimCorner2 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getKoth().setClaimBorder2(player.getLocation().clone());
                return setPearlsDisabled;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_koth_claim_corner_2"));
            }
        };
        Prompt setKothClaimCorner1 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getKoth().setClaimBorder1(player.getLocation().clone());
                return setKothClaimCorner2;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_koth_claim_corner_1"));
            }
        };
        Prompt setKothLowestCapzoneCorner = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getKoth().setLowestCapzoneCorner(player.getLocation().clone());
                return setKothClaimCorner1;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_koth_lowest_capzone_corner"));
            }
        };
        Prompt setKothHighestCapzoneCorner = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getKoth().setHighestCapzoneCorner(player.getLocation().clone());
                return setKothLowestCapzoneCorner;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_koth_highest_capzone_corner"));
            }
        };
        Prompt setSpectatorSpawn = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.setSpectatorSpawn(player.getLocation().clone());
                return setKothHighestCapzoneCorner;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_spectator_spawn"));
            }
        };
        //---
        //GREEN
        Prompt setGreenTeamSellShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getGreenTeam().setSellShop(player.getLocation().clone());
                return setYellowTeamHome;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_sell_shop").replace("%team%","&9BLUE"));
            }
        };
        Prompt setGreenTeamEquipmentShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getGreenTeam().setEquipmentShop(player.getLocation().clone());
                return setGreenTeamSellShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_equipment_shop").replace("%team%","&9BLUE"));
            }
        };
        Prompt setGreenTeamBlockShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getGreenTeam().setBlockShop(player.getLocation().clone());
                return setGreenTeamEquipmentShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_block_shop").replace("%team%","&9BLUE"));
            }
        };
        Prompt setGreenTeamClaimBorder2 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getGreenTeam().setClaimBorder2(player.getLocation().clone());
                return setGreenTeamBlockShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_claim_border_2").replace("%team%","&9BLUE"));
            }
        };
        Prompt setGreenTeamClaimBorder1 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getGreenTeam().setClaimBorder1(player.getLocation().clone());
                return setGreenTeamClaimBorder2;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_claim_border_1").replace("%team%","&9BLUE"));
            }
        };
        Prompt setGreenTeamHome = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                arena.getGreenTeam().setHome(player.getLocation().clone());
                return setGreenTeamClaimBorder1;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_home").replace("%team%","&9BLUE"));
            }
        };
        //BLUE
        Prompt setBlueTeamSellShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getBlueTeam().setSellShop(player.getLocation().clone());
                return setGreenTeamHome;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_sell_shop").replace("%team%","&9BLUE"));
            }
        };
        Prompt setBlueTeamEquipmentShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getBlueTeam().setEquipmentShop(player.getLocation().clone());
                return setBlueTeamSellShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_equipment_shop").replace("%team%","&9BLUE"));
            }
        };
        Prompt setBlueTeamBlockShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getBlueTeam().setBlockShop(player.getLocation().clone());
                return setBlueTeamEquipmentShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_block_shop").replace("%team%","&9BLUE"));
            }
        };
        Prompt setBlueTeamClaimBorder2 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getBlueTeam().setClaimBorder2(player.getLocation().clone());
                return setBlueTeamBlockShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_claim_border_2").replace("%team%","&9BLUE"));
            }
        };
        Prompt setBlueTeamClaimBorder1 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getBlueTeam().setClaimBorder1(player.getLocation().clone());
                return setBlueTeamClaimBorder2;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_claim_border_1").replace("%team%","&9BLUE"));
            }
        };
        Prompt setBlueTeamHome = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                arena.getBlueTeam().setHome(player.getLocation().clone());
                return setBlueTeamClaimBorder1;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_home").replace("%team%","&9BLUE"));
            }
        };
        //RED
        Prompt setRedTeamSellShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getRedTeam().setSellShop(player.getLocation().clone());
                return setBlueTeamHome;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_sell_shop").replace("%team%","&cRED"));
            }
        };
        Prompt setRedTeamEquipmentShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getRedTeam().setEquipmentShop(player.getLocation().clone());
                return setRedTeamSellShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_equipment_shop").replace("%team%","&cRED"));
            }
        };
        Prompt setRedTeamBlockShop = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getRedTeam().setBlockShop(player.getLocation().clone());
                return setRedTeamEquipmentShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_block_shop").replace("%team%","&cRED"));
            }
        };
        Prompt setRedTeamClaimBorder2 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getRedTeam().setClaimBorder2(player.getLocation().clone());
                return setRedTeamBlockShop;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_claim_border_2").replace("%team%","&cRED"));
            }
        };
        Prompt setRedTeamClaimBorder1 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.getRedTeam().setClaimBorder1(player.getLocation().clone());
                return setRedTeamClaimBorder2;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_claim_border_1").replace("%team%","&cRED"));
            }
        };
        Prompt setRedTeamHome = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                arena.getRedTeam().setHome(player.getLocation().clone());
                return setRedTeamClaimBorder1;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_team_home").replace("%team%","&cRED"));
            }
        };
        Prompt setArenaBorder2 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                arena.setBorder2(player.getLocation().clone());
                return setRedTeamHome;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_arena_border_2"));
            }
        };
        Prompt setArenaBorder1 = new FixedSetPrompt("done","cancel") {
            @Override
            protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                arena.setBorder1(player.getLocation().clone());
                return setArenaBorder2;
            }

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_arena_border_1"));
            }
        };
        Prompt setName = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Chat.transNoPrefix(PluginConfig.getMessages().get("set_arena_name"));
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                if (s.equalsIgnoreCase("cancel")){
                    arena = null;
                    return Prompt.END_OF_CONVERSATION;
                }
                arena.setName(Chat.transNoPrefix(s));
                return setArenaBorder1;
            }
        };
        factory.withFirstPrompt(setName).withModality(true).withLocalEcho(true).buildConversation(player).begin();
        return arena;
    }
}
