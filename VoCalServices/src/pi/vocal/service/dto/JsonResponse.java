package pi.vocal.service.dto;

/**
 * Wrapper class that are used by JSON WebService. This class will be mapped by
 * the framework Jackson, therefore it has to be a JavaBean-like class
 * 
 * @author s3ppl
 * 
 * @param <T>
 *            Type of the content the response should hold
 */
public class JsonResponse<T> {

	// JSON has no boolean, therefore C-like integers will be used
	private static final int JSON_TRUE = 1;
	private static final int JSON_FALSE = 0;

	/**
	 * 'boolean' value that tells the front end, that an operation was done
	 * successfully
	 */
	private int success;

	/**
	 * Generic content type that is the actual workload of the response
	 */
	private T content;

	public int getSuccess() {
		return success;
	}

/**
	 * Sets the value of {@code success} to the given value by converting the
	 * {@code boolean) value to its JSON representation.
	 * 
	 * @param isSuccessful
	 */
	public void setSuccess(boolean isSuccessful) {
		this.success = isSuccessful ? JSON_TRUE : JSON_FALSE;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

}
