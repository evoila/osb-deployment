package de.evoila.cf.cpi.bosh.deployment.manifest.addon;

import de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup.JobV2;

import java.util.List;

public class Addon {

    private String name;
    private List<JobV2> jobs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JobV2> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobV2> jobs) {
        this.jobs = jobs;
    }
}
