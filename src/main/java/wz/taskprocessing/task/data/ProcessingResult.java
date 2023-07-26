package wz.taskprocessing.task.data;

import java.util.Objects;

public class ProcessingResult {
    private Integer position = null, typos = null;

    public ProcessingResult() {}

    public ProcessingResult(int position) {
        this();
        this.position = position;
    }

    public ProcessingResult(Integer position, Integer typos) {
        this(position);
        this.typos = typos;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getTypos() {
        return typos;
    }

    public void setTypos(Integer typos) {
        this.typos = typos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingResult processingResult = (ProcessingResult) o;
        return Objects.equals(getPosition(), processingResult.getPosition()) && Objects.equals(getTypos(), processingResult.getTypos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(), getTypos());
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "position=" + position +
                ", typos=" + typos +
                '}';
    }
}
