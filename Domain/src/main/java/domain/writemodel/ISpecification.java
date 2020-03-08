package domain.writemodel;

public interface ISpecification<T> {
    boolean isSatisfiedBy(T value);
}
