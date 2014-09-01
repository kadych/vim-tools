package watcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.StandardWatchEventKinds.*;

public class App implements Runnable {
    private final WatchService watchService;

    public App() throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        final Path file = Paths.get("D:/temp");
        Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
                dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
        Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
                dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                break;
            }
            if (key != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == OVERFLOW) {
                        continue;
                    }

                    Path file =
                        ((Path) key.watchable()).resolve((Path) event.context())
                            .toAbsolutePath().normalize();

                    System.out.println(event.kind() + " " + file);
                }
                if (!key.reset()) {
                    // key invalid now
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Thread thread = new Thread(new App());
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}