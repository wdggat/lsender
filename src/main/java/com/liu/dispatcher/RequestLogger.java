package com.liu.dispatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.liu.message.Message;

public class RequestLogger {
    private static final Logger logger = Logger.getLogger(RequestLogger.class);

    private static String msgDonePath;
    private static String msgFailedPath;

    private static RequestLogger requestLogger = null;

    public static RequestLogger getRequestLogger() {
        if (requestLogger == null)
            return new RequestLogger();
        return requestLogger;
    }

    public static boolean init() {
        Configuration conf = new Configuration();
        msgDonePath = conf.getMsgDoneLogf();
        msgFailedPath = conf.getMsgFailLogf();

        if (createFolder(msgDonePath) &&
                createFolder(msgFailedPath))
            return true;
        else
            return false;
    }

    private static boolean createFolder(String folderPath) {
        File file = new File(folderPath);
        if (file.exists()) {
            logger.debug("Directory already exists");
            return true;
        }

        if (!file.mkdirs()) {
            logger.error("Failed to create directory " +
                    file.getParentFile().getAbsolutePath());
            return false;
        } else {
            logger.info("Directory "
                    + file.getParentFile().getAbsolutePath()
                    + " is created");
            return true;
        }
    }

    protected RequestLogger() {

    }

    public void logMsgDone(Message msg, int id) {
        writeToFile(msgDonePath, msg, id);
    }

    public void logMsgFailed(Message msg, int id) {
        writeToFile(msgFailedPath, msg, id);
    }

    private String getDayStringOfToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(Calendar.getInstance().getTime());
    }

    protected boolean writeToFile(String filePath, Message msg, int id) {
        String dayStr = getDayStringOfToday();
        File file = new File(filePath + '/' + dayStr + '-' + String.valueOf(id));

        PrintStream out = null;
        try {
            // Append to the file
            out = new PrintStream(new FileOutputStream(file, true));
            out.println(msg.toJson());
            closeOutputSteam(out);
        } catch (IOException e) {
            logger.error("Failed to write InputRequest to " + filePath);
            closeOutputSteam(out);
            return false;
        }
        return true;
    }

    private static void closeOutputSteam(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                logger.error("Can't close output stream", e);
            }
        }
    }
}
