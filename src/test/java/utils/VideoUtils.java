package utils;

import org.monte.screenrecorder.ScreenRecorder;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

public class VideoUtils {

    public static void stopAndProcessAsync(ScreenRecorder recorder, String videoDir,
                                           String testName, String status, String timestamp,
                                           java.util.function.Consumer<String> onDone) {
        // Run in background — does NOT block the next test from starting
        Thread thread = new Thread(() -> {
            try {
                if (recorder != null) recorder.stop();

                File[] avis = new File(videoDir).listFiles((dir, name) -> name.endsWith(".avi"));
                if (avis == null || avis.length == 0) return;

                File latestAvi = avis[0];
                for (File f : avis) if (f.lastModified() > latestAvi.lastModified()) latestAvi = f;

                File mp4 = new File(videoDir + testName + "_" + status + "_" + timestamp + ".mp4");
                Process p = new ProcessBuilder(
                        "ffmpeg", "-y", "-i", latestAvi.getAbsolutePath(),
                        "-vcodec", "libx264", "-crf", "28", "-preset", "ultrafast",
                        mp4.getAbsolutePath()
                ).redirectErrorStream(true).start();
                p.waitFor();

                if (mp4.exists() && mp4.length() > 0) {
                    String b64 = Base64.getEncoder().encodeToString(Files.readAllBytes(mp4.toPath()));
                    String videoTag = "<video width='600' controls><source src='data:video/mp4;base64,"
                            + b64 + "' type='video/mp4'></video>";
                    if (onDone != null) onDone.accept(videoTag);
                }
            } catch (Exception e) {
                System.err.println("Video processing failed: " + e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
