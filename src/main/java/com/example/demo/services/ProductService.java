package com.example.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DTO.CategoryDTO;
import com.example.demo.DTO.ProductDTO;
import com.example.demo.entitties.Category;
import com.example.demo.entitties.Product;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.services.exceptions.DatabaseException;
import com.example.demo.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

  @Autowired
  private ProductRepository repository;
  @Autowired
  private CategoryRepository categoryRepository;

  @Transactional(readOnly = true)
  public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
    Page<Product> list = repository.findAll(pageRequest);
    return list.map(x -> new ProductDTO(x));
  }

  @Transactional(readOnly = true)
  public ProductDTO findById(Long id) {
    Optional<Product> obj = repository.findById(id);
    Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found."));
    return new ProductDTO(entity, entity.getCategories());
  }

  @Transactional
  public ProductDTO insert(ProductDTO DTO) {
    Product entity = new Product();
    copyDTOToEntity(DTO, entity);
    entity = repository.save(entity);
    return new ProductDTO(entity);
  }

  private void copyDTOToEntity(ProductDTO DTO, Product entity) {
    entity.setName(DTO.getName());
    entity.setDescription(DTO.getDescription());
    entity.setDate(DTO.getDate());
    entity.setImgUrl(DTO.getImgUrl());
    entity.setPrice(DTO.getPrice());

    entity.getCategories().clear();
    for (CategoryDTO catDto : DTO.getCategories()) {
      Category category = categoryRepository.getOne(catDto.getId());
      entity.getCategories().add(category);

    }
  }

  @Transactional
  public ProductDTO update(Long id, ProductDTO DTO) {
    try {
      Product entity = repository.getReferenceById(id);
      copyDTOToEntity(DTO, entity);
      entity = repository.save(entity);
      return new ProductDTO(entity);
    }

    catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Id " + id + " not found ");
    }
  }

  public void delete(Long id) {
    try {
      repository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Id not found " + id);
    } catch (DataIntegrityViolationException e) {
      throw new DatabaseException("Integrity violation");
    }
  }

}

// List<ProductDTO> listDTO = new ArrayList();
// for (Product cat: list) {
// listDTO.add(new ProductDTO(cat));
// }
// return listDTO;
// }
