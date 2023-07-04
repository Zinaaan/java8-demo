package common;

import java.util.Objects;

/**
 * @author lzn
 * @date 2023/05/23 11:18
 * @description The result object wrapper for every request
 */
public class Result {

    private final int returnCode;

    private final String returnMessage;

    public Result(int returnCode, String returnMessage) {
        this.returnCode = returnCode;
        this.returnMessage = returnMessage;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result result = (Result) o;
        return returnCode == result.returnCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnCode);
    }

    @Override
    public String toString() {
        return "Result{" +
                "returnCode=" + returnCode +
                ", returnMessage='" + returnMessage + '\'' +
                '}';
    }
}
