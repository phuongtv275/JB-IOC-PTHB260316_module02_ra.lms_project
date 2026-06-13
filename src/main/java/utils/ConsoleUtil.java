package utils;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUtil {

    private static final Scanner scanner = new Scanner(System.in);

    // ── ANSI colors ──────────────────────────────────────────────
    public static final String RESET  = "\033[0m";
    public static final String BOLD   = "\033[1m";
    public static final String CYAN   = "\033[36m";
    public static final String GREEN  = "\033[32m";
    public static final String RED    = "\033[31m";
    public static final String YELLOW = "\033[33m";
    public static final String WHITE  = "\033[97m";

    private ConsoleUtil() {}

    // ── Input ────────────────────────────────────────────────────

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Vui lòng nhập số nguyên hợp lệ.");
            }
        }
    }

    // ── Print helpers ────────────────────────────────────────────

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "✔  " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "✘  " + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + "⚠  " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "ℹ  " + msg + RESET);
    }

    public static void printDivider() {
        System.out.println(CYAN + "─".repeat(60) + RESET);
    }

    public static void printTitle(String title) {
        System.out.println();
        printDivider();
        System.out.println(BOLD + CYAN + "  " + title + RESET);
        printDivider();
    }

    // ── Menu builder ─────────────────────────────────────────────

    /**
     * In menu với tiêu đề và danh sách lựa chọn.
     * options[0] là lựa chọn 1, options[1] là lựa chọn 2, ...
     * Lựa chọn 0 luôn là "Quay lại / Thoát" — truyền vào qua backLabel.
     */
    public static void printMenu(String title, String[] options, String backLabel) {
        printTitle(title);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("  %s[%d]%s %s%n", YELLOW, i + 1, RESET, options[i]);
        }
        System.out.printf("  %s[0]%s %s%n", YELLOW, RESET, backLabel);
        printDivider();
    }

    /**
     * In bảng dữ liệu.
     * @param headers  Tên các cột
     * @param rows     Dữ liệu: mỗi phần tử là một hàng, mỗi hàng là mảng String
     * @param widths   Độ rộng tương ứng với mỗi cột
     */
    public static void printTable(String[] headers, String[][] rows, int[] widths) {
        StringBuilder separator = new StringBuilder("+");
        for (int w : widths) {
            separator.append("-".repeat(w + 2)).append("+");
        }
        String sep = separator.toString();

        System.out.println(CYAN + sep + RESET);

        // Header row
        StringBuilder headerRow = new StringBuilder(BOLD + CYAN + "|");
        for (int i = 0; i < headers.length; i++) {
            headerRow.append(String.format(" %-" + widths[i] + "s |", headers[i]));
        }
        System.out.println(headerRow + RESET);
        System.out.println(CYAN + sep + RESET);

        // Data rows
        if (rows == null || rows.length == 0) {
            int totalWidth = sep.length();
            System.out.printf(YELLOW + "| %-" + (totalWidth - 4) + "s |%n" + RESET, "Không có dữ liệu.");
        } else {
            for (String[] row : rows) {
                StringBuilder dataRow = new StringBuilder("|");
                for (int i = 0; i < headers.length; i++) {
                    String cell = (i < row.length && row[i] != null) ? row[i] : "";
                    dataRow.append(String.format(" %-" + widths[i] + "s |", cell));
                }
                System.out.println(dataRow);
            }
        }

        System.out.println(CYAN + sep + RESET);
    }

    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Executes the Windows shell built-in cls command
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Executes the Unix clear command
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Could not clear the screen.");
        }
    }

    public static void pressEnterToContinue() {
        System.out.print(YELLOW + "\n  Nhấn Enter để tiếp tục..." + RESET);
        scanner.nextLine();
        clearScreen();
    }

    public static Scanner getScanner() {
        return scanner;
    }
}

