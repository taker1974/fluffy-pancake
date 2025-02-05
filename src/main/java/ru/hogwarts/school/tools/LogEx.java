// Copyright TKSoft, Konstantin Terskikh, kostus.online.1974@yandex.ru, 2025
// Permission is hereby granted,free of charge,
// to any person obtaining a copy of this software and associated documentation files(the"Software"),
// to deal in the Software without restriction,including without limitation the rights to use,copy,modify,merge,
// publish,distribute,sublicense,and/or sell copies of the Software,
// and to permit persons to whom the Software is furnished to do so,subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// THE SOFTWARE IS PROVIDED"AS IS",WITHOUT WARRANTY OF ANY KIND,EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM,DAMAGES OR OTHER LIABILITY,WHETHER IN AN ACTION OF CONTRACT,TORT OR OTHERWISE,ARISING
// FROM,OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package ru.hogwarts.school.tools;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Arrays;

/**
 * LogEx.
 *
 * @author Konstantin Terskikh, kostus.online.1974@yandex.ru, 2025
 * @version 0.1
 */
public class LogEx {

    public static final String STARTING = "запускается";
    public static final String STOPPING = "завершается";
    public static final String STOPPED = "завершён";
    public static final String SHORT_RUN = "запускается и завершается";

    public static final String EXCEPTION_THROWN = "выброшено исключение";

    private LogEx() {
    }

    public static String getThisMethodName() {

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        int currentFrameIndex = 2; // 0 - getStackTrace(), 1 - getCurrentMethodName()
        if (stackTraceElements.length > currentFrameIndex) {
            return stackTraceElements[currentFrameIndex].getMethodName();
        } else {
            throw new IllegalStateException("Стек вызовов слишком короткий");
        }
    }

    public static void log(Logger logger, Level level, Object[] parts) {
        final String[] strings = Arrays.stream(parts).map(String::valueOf).toArray(String[]::new);
        String message = String.join(": ", strings);
        switch (level) {
            case TRACE -> logger.trace(message);
            case DEBUG -> logger.debug(message);
            case INFO -> logger.info(message);
            case WARN -> logger.warn(message);
            case ERROR -> logger.error(message);
        }
    }

    public static void trace(Logger logger, Object... parts) {
        log(logger, Level.TRACE, parts);
    }

    public static void debug(Logger logger, Object... parts) {
        log(logger, Level.DEBUG, parts);
    }

    public static void info(Logger logger, Object... parts) {
        log(logger, Level.INFO, parts);
    }

    public static void warn(Logger logger, Object... parts) {
        log(logger, Level.WARN, parts);
    }

    public static void error(Logger logger, Object... parts) {
        log(logger, Level.ERROR, parts);
    }
}
