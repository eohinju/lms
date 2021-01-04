package tz.mil.ngome.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tz.mil.ngome.lms.entity.Contribution;

public interface ContributionRepository  extends JpaRepository<Contribution, String> {

}
