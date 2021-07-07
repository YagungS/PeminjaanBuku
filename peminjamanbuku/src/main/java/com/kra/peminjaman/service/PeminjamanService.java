package com.kra.peminjaman.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kra.peminjaman.repository.BukuRepository;
import com.kra.peminjaman.repository.MahasiswaRepository;
import com.kra.peminjaman.model.Mahasiswa;
import com.kra.peminjaman.model.Buku;

@Service
public class PeminjamanService {
	@Autowired
    private MahasiswaRepository mhsRepository;
 
    @Autowired
    private BukuRepository bookRepository;
 
    public List<Mahasiswa> getAllMahasiswa() {
 
        return mhsRepository.findAll();
    }
    public List<Buku> getAllBuku() {
    	 
        return bookRepository.findAll();
    }
 
    public Boolean isExistsMahasiswa(Mahasiswa mahasiswa) {
    	List<Mahasiswa> allMhs = mhsRepository.findAll();
    	for(int i=0;i<allMhs.size();i++) {
    		if(String.valueOf(allMhs.get(i).getName()).equals(mahasiswa.getName())) {
    			return true;
    		}
    	}
        return false;
    }
    
    public Boolean isExistsBuku(Buku book) {
    	List<Buku> allBook = bookRepository.findAll();
    	for(int i=0;i<allBook.size();i++) {
    		if(String.valueOf(allBook.get(i).getName()).equals(book.getName())) {
    			return true;
    		}
    	}
        return false;
    }
    
    public Mahasiswa pinjamBuku(Mahasiswa mhs, Buku book) {
    	 if (mhs != null && book !=null) {
    		 {
            	 Set<Buku> books = mhs.getBooks();
        		 books.add(book);
        		 mhs.setBooks(books);
        		 mhs.setBookCounter(mhs.getBookCounter()+1);
        		 book.setMahasiswa(mhs);
        		 bookRepository.save(book);
    		 }
         }
         else {
        	 return null;
         }
		 return mhsRepository.save(mhs);
    }
    
    public Mahasiswa pengembalianBuku(Mahasiswa mhs, Buku book) {
	    if (mhs != null && book !=null) {
	 		if(book.getMahasiswa() != null) {       	 			
	           	 mhs.getBooks().remove(book);
	           	 mhs.setBookCounter(mhs.getBookCounter() == 0?0:mhs.getBookCounter()-1);
	       		 book.setMahasiswa(null);
	       		 bookRepository.save(book);
	 		}
	    }
	    else {
	   	 	return null;
	    }
		 return mhsRepository.save(mhs);
   }

    public Boolean isBukuFree(Buku book) {
    	return (book.getMahasiswa() == null);
    }
    
    public Boolean isOnPinjamanList(Mahasiswa mhs, Buku book) {
		return mhs.getBooks().contains(book);
    }
}
