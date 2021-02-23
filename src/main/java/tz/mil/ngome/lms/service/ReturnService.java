package tz.mil.ngome.lms.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import tz.mil.ngome.lms.entity.Loan;
import tz.mil.ngome.lms.entity.Member;

@Service
public interface ReturnService {

	public void saveValidReturn(Loan loan, int amount, LocalDate date);
	public void saveInValidReturn(Member member, int amount, LocalDate date);

}
