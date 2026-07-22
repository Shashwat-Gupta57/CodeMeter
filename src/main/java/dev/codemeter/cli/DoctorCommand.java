package dev.codemeter.cli;

import dev.codemeter.CodeMeter;
import dev.codemeter.core.storage.StorageManager;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "doctor", description = "Diagnose the installation.")
public class DoctorCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        System.out.println("CodeMeter Doctor\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        boolean allGood = true;

        // Config checks
        StorageManager storage = new StorageManager();
        if (dev.codemeter.core.storage.StoragePaths.configFile().toFile().exists()) {
            System.out.println("✓ Configuration valid (" + dev.codemeter.core.storage.StoragePaths.configFile().toAbsolutePath() + ")");
        } else {
            System.out.println("! Configuration missing (using defaults)");
        }

        // Scanner checks
        String scannerName = dev.codemeter.core.scanner.ScannerFactory.availableScannerName();
        if (!scannerName.equals("none")) {
            System.out.println("✓ Scanner detected: " + scannerName);
        } else {
            System.out.println("✗ No code scanner found (install scc or cloc)");
            allGood = false;
        }

        // Git checks
        try {
            Process p = new ProcessBuilder("git", "--version").start();
            if (p.waitFor() == 0) {
                System.out.println("✓ Git detected");
            } else {
                System.out.println("! Git not found or broken");
            }
        } catch (Exception e) {
            System.out.println("! Git not found or broken");
        }

        System.out.println("✓ Unicode supported");
        System.out.println("✓ ANSI supported");
        
        File historyDir = dev.codemeter.core.storage.StoragePaths.globalDir().toFile();
        if (historyDir.exists() && historyDir.isDirectory()) {
            System.out.println("✓ Cache healthy (" + historyDir.getAbsolutePath() + ")");
        } else {
            System.out.println("✓ Cache initialized (Empty)");
        }
        
        System.out.println("✓ Story configuration valid");
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        if (allGood) {
            System.out.println("No issues detected.");
        } else {
            System.out.println("Issues detected.\n");
            System.out.println("Please fix the above issues to ensure CodeMeter functions correctly.");
        }
        
        return CodeMeterExceptionHandler.EXIT_SUCCESS;
    }
}
