package pi.vocal.service;

/**
 * Wrapper class that are used by JSON webservices. This class will be mapped by
 * jackson, therefore it has to be a POJO class
 * 
 * @author s3ppl
 * 
 * @param <T> Type of the content the response should hold
 */
public class JsonResponse<T> {

	// json has no boolean, therefore C-like integers will be used
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

	public void setSuccess(boolean isSuccessful) {
		// mapping from boolean to json
		this.success = isSuccessful ? JSON_TRUE : JSON_FALSE;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

}
