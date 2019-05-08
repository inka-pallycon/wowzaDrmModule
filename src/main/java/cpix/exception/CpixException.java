package cpix.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cpix.exception.dto.ErrorDTO;

import java.util.Optional;
/**
 * Created by Brown on 2019-04-26.
 */
public class CpixException extends Exception {
    private static final long serialVersionUID = 31647601034584970L;
    private ErrorDTO errorDTO;
    static String message;

    public CpixException(String message) {
        CpixException.message = message;
    }

    public CpixException(ErrorDTO errorDTO) {
        this.errorDTO = errorDTO;
    }

    public String getMessage() {
        String result = (String) Optional.ofNullable(message).orElse("");
        if (null != this.errorDTO) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                result = mapper.writeValueAsString(this.errorDTO);
            } catch (JsonProcessingException var4) {
                var4.printStackTrace();
            }
        }

        return result;
    }

    public void setMessage(String message) {
        CpixException.message = message;
    }

    public String toString() {
        return "CpixException{message=\'" + message + '\'' + '}';
    }
}