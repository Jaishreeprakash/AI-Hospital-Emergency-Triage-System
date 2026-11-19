package com.hospital.triage;

import com.hospital.triage.repository.BedRepository;
import com.hospital.triage.repository.PatientRepository;
import com.hospital.triage.repository.StaffRepository;
import com.hospital.triage.service.HospitalModeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HospitalTriageApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalTriageApplication.class, args);
    }

    @Bean
    public CommandLineRunner initHospitalSystem(
            StaffRepository staffRepo,
            PatientRepository patientRepo,
            BedRepository bedRepo,
            HospitalModeService modeService) {
        return args -> {
            System.out.println("====================================================================");
            System.out.println(" AI-ENABLED HOSPITAL RESOURCE AND EMERGENCY TRIAGE SYSTEM (CSA1104) ");
            System.out.println(" OOAD Design Patterns: Strategy | State | Observer Initialized      ");
            System.out.println("====================================================================");
            System.out.printf(" Staff Registered: %d | Wards & Beds Active: %d | Patients: %d%n",
                    staffRepo.count(), bedRepo.count(), patientRepo.count());
            System.out.printf(" Current Operating Mode: %s (%s)%n",
                    modeService.getCurrentMode().name(), modeService.getCurrentMode().getTitle());
            System.out.println(" System API Gateway: http://localhost:8080/api");
            System.out.println(" Hospital SaaS Dashboard: http://localhost:8080/");
            System.out.println("====================================================================");
        };
    }
}
