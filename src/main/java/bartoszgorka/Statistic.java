package bartoszgorka;

public class Statistic {
    private Integer bytesSent;
    private Integer bytesReceived;
    private Integer requests;
    private String domain;

    /**
     * @param domain           Domain
     * @param bytesSent        Bytes sent to domain
     * @param bytesReceived    Bytes received from domain
     * @param amountOfRequests Amount of requests
     */
    public Statistic(String domain, Integer bytesSent, Integer bytesReceived, Integer amountOfRequests) {
        this.domain = domain;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.requests = amountOfRequests;
    }

    /**
     * @return Bytes received from domain
     */
    public Integer getBytesReceived() {
        return bytesReceived;
    }

    /**
     * @return Bytes sent to domain
     */
    public Integer getBytesSent() {
        return bytesSent;
    }

    /**
     * @return Domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return Amount of requests
     */
    public Integer getRequests() {
        return requests;
    }
}
