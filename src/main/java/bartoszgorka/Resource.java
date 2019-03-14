package bartoszgorka;

public class Resource {
    private String mimeType;
    private int statusCode;
    private byte[] message;

    public Resource(int code, byte[] message, String mime) {
        this.statusCode = code;
        this.message = message.clone();
        this.mimeType = mime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getMessage() {
        return message;
    }

    public String getMimeType() {
        return mimeType;
    }
}
