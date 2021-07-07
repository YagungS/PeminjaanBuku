package com.kra.peminjaman.model;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;


@Entity
@Table(name = "Mahasiswa")
public class Mahasiswa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;

	@Column(name = "bookcunter")
	private int bookCounter;
	
	@OneToMany(mappedBy = "mahasiswa", cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<Buku> books = new HashSet<>();
	
	public Mahasiswa() {
		super();
	}
	public Mahasiswa(String name) {
		super();
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Buku> getBooks() {
		return books;
	}

	public void setBooks(Set<Buku> books) {
		this.books = books;

        for(Buku b : books) {
            b.setMahasiswa(this);
        }
	}

	public int getBookCounter() {
		return bookCounter;
	}

	public void setBookCounter(int bookCounter) {
		this.bookCounter = bookCounter;
	}


}
