package dev.suresh.adt;


/**
 * A discriminated union that encapsulates a successful outcome with a
 * value of type T or a failure with an arbitrary Throwable exception.
 *
 * @param <T> Result type
 */
sealed class Result<T> permits Failure{

 private final T value;

 public Result(T value) {
   this.value = value;
 }

 public boolean isSuccess() {
    return !isFailure();
 }

 public boolean isFailure() {
  return value instanceof Failure;
 }

 public T getOrNull() {
    return isFailure() ? null : value;
 }

 public Throwable exceptionOrNull() {
   return value instanceof Failure t ? t.error() : null;
 }

 public static <T> Result<T> success(T value) {
   return new Result<>(value) ;
 }

 public static <T> Result<T> failure(Throwable error) {
  return new Result<>((T) new Failure(error)) ;
 }

 @Override
 public String toString() {
  return value instanceof Failure t ? t.toString() : "Success(%s)".formatted(value);
 }

}

final class Failure extends Result<Void>{

 private final Throwable error;

 public Failure(Throwable error) {
  super(null);
  this.error = error;
 }

 public Throwable error() {
  return error;
 }

 @Override
 public String toString() {
  return "Failure(%s)".formatted(error);
 }
}


 sealed interface SealedInf permits TestInf {

 }

 final record TestInf(String name) implements SealedInf {

 }

 public class ADTSamples {

  public static void main(String[] args){
     var s = Result.success("success");
     print(s);
     var e = Result.failure(new IllegalAccessError("error"));
     print(e);
  }

  static <T> void print(Result<T> r) {
    System.out.println(r);
    System.out.println("Value    -> " + r.getOrNull());
    System.out.println("Success  -> " + r.isSuccess());
    System.out.println("Failure   -> " + r.isFailure());
    System.out.println("Exception -> " + r.exceptionOrNull());
  }
}
