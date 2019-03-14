package bartoszgorka;

public class Resource {
    private String mimeType;
    private int statusCode;
    private byte[] message;

    /**
     * Resource content
     *
     * @param code    Response HTTP Code
     * @param message Byte message
     * @param mime    File MIME type, can be null
     */
    public Resource(int code, byte[] message, String mime) {
        this.statusCode = code;
        this.message = message.clone();
        this.mimeType = mime;
    }

    /**
     * @return HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return Byte message
     */
    public byte[] getMessage() {
        return message;
    }

    /**
     * @return Content MIME type
     */
    public String getMimeType() {
        return mimeType;
    }
}
