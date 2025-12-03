package ru.hse.antiplag.gateway.dto;

public class WorkWithReportDto {

    private StoredWorkDto storedWork;
    private ReportDto report;

    public WorkWithReportDto() {
    }

    public WorkWithReportDto(StoredWorkDto storedWork, ReportDto report) {
        this.storedWork = storedWork;
        this.report = report;
    }

    public StoredWorkDto getStoredWork() {
        return storedWork;
    }

    public void setStoredWork(StoredWorkDto storedWork) {
        this.storedWork = storedWork;
    }

    public ReportDto getReport() {
        return report;
    }

    public void setReport(ReportDto report) {
        this.report = report;
    }
}
