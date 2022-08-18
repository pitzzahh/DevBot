/*
 * MIT License
 *
 * Copyright (c) 2022 pitzzahh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import javax.security.auth.login.LoginException;
import club.minnced.discord.rpc.DiscordRPC;
import com.github.pitzzahh.utilities.Print;
import java.util.concurrent.TimeUnit;
import io.github.pitzzahh.Bot;

public class App {


    public static void main(String[] args) throws LoginException {
        new Bot();
        intelliJ();
    }

    /**
     * Updates my discord presence
     */
    private static void runningTheBot() {
        var lib = DiscordRPC.INSTANCE;
        var applicationId = Bot.getConfig().get("APP_ID_BOT");
        var steamId = Bot.getConfig().get("STEAM_ID");
        var handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> Print.printf("%s is ready", user.username);
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.state = "Coding solo";
        presence.details = "Competitive";
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.largeImageKey = "pizza";
        presence.largeImageText = "[;] pitzzahh-bot icon";
        presence.partyId = "ae488379-351d-4a4f-ad32-2b9b01c91657";
        presence.partySize = 1;
        presence.partyMax = 10;
        presence.joinSecret = "MTI4NzM0OjFpMmhuZToxMjMxMjM= ";
        lib.Discord_UpdatePresence(presence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "RPC-Callback-Handler").start();
    }

    /**
     * Updates my discord presence
     */
    private static void intelliJ() {
        var lib = DiscordRPC.INSTANCE;
        var applicationId = Bot.getConfig().get("APP_ID_IDEA");
        var steamId = Bot.getConfig().get("STEAM_ID");
        var handlers = new DiscordEventHandlers();
        handlers.ready = (user) -> Print.printf("%s is ready", user.username);
        lib.Discord_Initialize(applicationId, handlers, true, steamId);
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.state = "Coding [;] pitzzahh-bot";
        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
        presence.largeImageKey = "idea";
        presence.largeImageText = "IntelliJ IDEA Community Edition";
        lib.Discord_UpdatePresence(presence);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lib.Discord_RunCallbacks();
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "RPC-Callback-Handler").start();
    }
}
