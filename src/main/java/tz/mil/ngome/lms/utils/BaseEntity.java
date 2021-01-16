package tz.mil.ngome.lms.utils;


import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
public class BaseEntity {
	
	@Id
	@Column(name = "id", nullable = false, unique = true)
	private String id = CustomData.GenerateUniqueID();
	
	@Basic(optional = false)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Basic(optional = true)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Basic(optional = false)
	@Column(name = "created_by")
	private String createdBy = null;

	@Basic(optional = true)
	@Column(name = "updated_by")
	private String updatedBy = null;

	@Basic(optional = false)
	@Column(name = "deleted")
	private Boolean deleted = false;

	@Basic(optional = true)
	@Column(name = "deleted_by")
	private String deletedBy;

	@Basic(optional = true)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

}
