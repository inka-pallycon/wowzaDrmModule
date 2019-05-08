package cpix.exception.dto;

/**
 * Created by Brown on 2019-04-26.
 */
public class ErrorDTO {
    private static final long serialVersionUID = 31647601034584969L;
    String code;
    String message;
    String body;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorDTO(String code, String message, String body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }
}
