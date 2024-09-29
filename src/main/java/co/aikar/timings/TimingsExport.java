/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Daniel Ennis <http://aikar.co>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package co.aikar.timings;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.sculk.Sculk;
import org.sculk.Server;
import org.sculk.timings.JsonUtil;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import static co.aikar.timings.TimingsManager.HISTORY;

@Log4j2
public class TimingsExport extends Thread {

    private final ObjectNode out;
    private final TimingsHistory[] history;

    private TimingsExport(ObjectNode out, TimingsHistory[] history) {
        super("Timings paste thread");
        this.out = out;
        this.history = history;
    }

    public static void reportTimings() {
        ObjectNode out = Sculk.JSON_MAPPER.createObjectNode();
        out.put("version", Server.getInstance().getVersion());
        out.put("maxplayers", Server.getInstance().getMaxPlayers());
        out.put("start", TimingsManager.timingStart / 1000);
        out.put("end", System.currentTimeMillis() / 1000);
        out.put("sampletime", (System.currentTimeMillis() - TimingsManager.timingStart) / 1000);

        if (!Timings.isPrivacy()) {
            out.put("server", Server.getInstance().getMotd());
            out.put("motd", Server.getInstance().getMotd());
            out.put("online-mode", Server.getInstance().isXboxAuth());
            out.put("icon", ""); //"data:image/png;base64,"
        }

        final Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        ObjectNode system = Sculk.JSON_MAPPER.createObjectNode();
        system.put("timingcost", getCost());
        system.put("name", System.getProperty("os.name"));
        system.put("version", System.getProperty("os.version"));
        system.put("jvmversion", System.getProperty("java.version"));
        system.put("arch", System.getProperty("os.arch"));
        system.put("maxmem", runtime.maxMemory());
        system.put("cpu", runtime.availableProcessors());
        system.put("runtime", ManagementFactory.getRuntimeMXBean().getUptime());
        system.put("flags", String.join(" ", runtimeBean.getInputArguments()));
        system.set("gc", JsonUtil.mapToObject(ManagementFactory.getGarbageCollectorMXBeans(), (input) ->
                new JsonUtil.JSONPair(input.getName(), JsonUtil.toArray(input.getCollectionCount(), input.getCollectionTime()))));
        out.set("system", system);

        TimingsHistory[] history = HISTORY.toArray(new TimingsHistory[HISTORY.size() + 1]);
        history[HISTORY.size()] = new TimingsHistory(); //Current snapshot

        ObjectNode timings = Sculk.JSON_MAPPER.createObjectNode();
        for (TimingIdentifier.TimingGroup group : TimingIdentifier.GROUP_MAP.values()) {
            for (Timing id : group.timings) {
                if (!id.timed && !id.isSpecial()) {
                    continue;
                }

                timings.set(String.valueOf(id.id), JsonUtil.toArray(group.id, id.name));
            }
        }

        new TimingsExport(out, history).start();
    }

    private static long getCost() {
        int passes = 200;
        Timing SAMPLER1 = TimingsManager.getTiming(null, "Timings sampler 1", null);
        Timing SAMPLER2 = TimingsManager.getTiming(null, "Timings sampler 2", null);
        Timing SAMPLER3 = TimingsManager.getTiming(null, "Timings sampler 3", null);
        Timing SAMPLER4 = TimingsManager.getTiming(null, "Timings sampler 4", null);
        Timing SAMPLER5 = TimingsManager.getTiming(null, "Timings sampler 5", null);
        Timing SAMPLER6 = TimingsManager.getTiming(null, "Timings sampler 6", null);

        long start = System.nanoTime();
        for (int i = 0; i < passes; i++) {
            SAMPLER1.startTiming();
            SAMPLER2.startTiming();
            SAMPLER3.startTiming();
            SAMPLER4.startTiming();
            SAMPLER5.startTiming();
            SAMPLER6.startTiming();
            SAMPLER6.stopTiming();
            SAMPLER5.stopTiming();
            SAMPLER4.stopTiming();
            SAMPLER3.stopTiming();
            SAMPLER2.stopTiming();
            SAMPLER1.stopTiming();
        }

        long timingsCost = (System.nanoTime() - start) / passes / 6;

        SAMPLER1.reset(true);
        SAMPLER2.reset(true);
        SAMPLER3.reset(true);
        SAMPLER4.reset(true);
        SAMPLER5.reset(true);
        SAMPLER6.reset(true);

        return timingsCost;
    }

    @Override
    public void run() {}

    private String getResponse(HttpURLConnection con) {
        return null;
    }
}
