/*
 * Copyright (C) 2013 Lord_Ralex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ae97.relaybot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NoticeEvent;

public class RelayBot extends ListenerAdapter {

    private static String name = "RelayBot";
    private static String pass = "";
    private static String ip = "irc.esper.net";
    private static String port = "6667";
    private static List<String> channels = new ArrayList<String>();
    private static String listenChannel = "";

    public static void main(String[] args) throws IOException, IrcException {
        if (args.length == 0) {
            Scanner reader = new Scanner(new File("config.yml"));
            List<String> lines = new ArrayList<String>();
            while (reader.hasNext()) {
                lines.add(reader.nextLine());
            }
            reader.close();
            String key = "";
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("-") && key.equalsIgnoreCase("target-chans")) {
                    channels.add(line.substring(1).trim());
                } else {
                    key = line.split(":")[0].trim();
                    String value = line.split(":")[1].trim();
                    if (value.isEmpty()) {
                        continue;
                    } else if (key.equalsIgnoreCase("nickname")) {
                        name = value;
                    } else if (key.equalsIgnoreCase("password")) {
                        pass = value;
                    } else if (key.equalsIgnoreCase("server-ip")) {
                        ip = value;
                    } else if (key.equalsIgnoreCase("server-port")) {
                        port = value;
                    } else if (key.equalsIgnoreCase("listen")) {
                        listenChannel = value;
                    }
                }
            }
        } else {
            System.exit(0);
        }
        PircBotX bot = new PircBotX();
        bot.setName(name);
        bot.setLogin(name);
        bot.connect(ip, Integer.parseInt(port));
        if (pass != null && !pass.isEmpty()) {
            bot.sendMessage("nickserv", "identify " + pass);
        }
        bot.getListenerManager().addListener(new RelayBot());
    }

    @Override
    public void onNotice(NoticeEvent event) throws Exception {
        if (event.getChannel().getName().equalsIgnoreCase(listenChannel)) {
            for (String dest : channels) {
                event.getBot().sendMessage(dest, event.getMessage());
            }
        }
    }
}
