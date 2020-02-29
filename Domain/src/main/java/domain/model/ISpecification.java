package domain.model;

public interface ISpecification<T> {
    boolean isSatisfiedBy(T value);
}
