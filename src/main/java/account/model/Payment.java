package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
@Table(name="PAYMENTS")
public class Payment {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;
    private String employee;
    @NotBlank
    private String period;
    private long salary;

    public Payment() {
    }

    public Payment(String employee, long salary, String period) {
        this.employee = employee;
        this.salary = salary;
        this.period = period;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Payment p = (Payment) obj;

        return this.getPeriod().equals(p.getPeriod()) &&
                this.getEmployee().equals(p.getEmployee());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
