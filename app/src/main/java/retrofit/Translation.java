package retrofit;

/**
 * Created by Carson_Ho on 17/3/20.
 */
public class Translation {

    private int status;

    private content content;

    @Override
    public String toString() {
        return "Translation{" +
                "status=" + status +
                ", content=" + content +
                '}';
    }

    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;

        @Override
        public String toString() {
            return "content{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", vendor='" + vendor + '\'' +
                    ", out='" + out + '\'' +
                    ", errNo=" + errNo +
                    '}';
        }
    }
}
