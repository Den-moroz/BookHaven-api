package core.basesyntax.bookstore.category;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import core.basesyntax.bookstore.dto.category.CategoryDto;
import core.basesyntax.bookstore.dto.category.CreateCategoryRequestDto;
import core.basesyntax.bookstore.exception.EntityNotFoundException;
import core.basesyntax.bookstore.mapper.CategoryMapper;
import core.basesyntax.bookstore.model.Category;
import core.basesyntax.bookstore.repository.category.CategoryRepository;
import core.basesyntax.bookstore.service.impl.CategoryServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final CreateCategoryRequestDto REQUEST_DTO = new CreateCategoryRequestDto();
    private static final Category VALID_CATEGORY = new Category();
    private static final CategoryDto VALID_RESPONSE = new CategoryDto();

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        REQUEST_DTO.setName("Category 1");
        REQUEST_DTO.setDescription("Description 1");

        VALID_CATEGORY.setId(1L);
        VALID_CATEGORY.setName("Category 1");
        VALID_CATEGORY.setDescription("Description 1");

        VALID_RESPONSE.setId(VALID_CATEGORY.getId());
        VALID_RESPONSE.setName(VALID_CATEGORY.getName());
        VALID_RESPONSE.setDescription(VALID_CATEGORY.getDescription());
    }

    @Test
    @DisplayName("Verify save() method works")
    void save_validRequestDto_returnCategoryDto() {
        when(categoryMapper.toModel(REQUEST_DTO)).thenReturn(VALID_CATEGORY);
        when(categoryRepository.save(VALID_CATEGORY)).thenReturn(VALID_CATEGORY);
        when(categoryMapper.toDto(VALID_CATEGORY)).thenReturn(VALID_RESPONSE);

        CategoryDto savedCategoryDto = categoryService.save(REQUEST_DTO);
        Assertions.assertEquals(VALID_RESPONSE, savedCategoryDto);
        verify(categoryRepository, times(1)).save(VALID_CATEGORY);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_includedPagination_returnListCategoryDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage =
                new PageImpl<>(Collections.singletonList(VALID_CATEGORY), pageable, 1);
        when(categoryRepository.findAll(pageable))
                .thenReturn(categoryPage);
        when(categoryMapper.toDto(VALID_CATEGORY)).thenReturn(VALID_RESPONSE);

        List<CategoryDto> actual = categoryService.findAll(pageable);
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(VALID_CATEGORY);
        Assertions.assertEquals(Collections.singletonList(VALID_RESPONSE), actual);
    }

    @Test
    @DisplayName("Test getById with existing category")
    void getById_validId_returnCategoryDto() {
        when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(VALID_CATEGORY));
        when(categoryMapper.toDto(VALID_CATEGORY)).thenReturn(VALID_RESPONSE);

        CategoryDto actual = categoryService.getById(1L);
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDto(VALID_CATEGORY);
        Assertions.assertEquals(VALID_RESPONSE, actual);
    }

    @Test
    @DisplayName("Test getById with non-existing category")
    void getById_invalidId_throwException() {
        when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(100L));
        verify(categoryRepository).findById(100L);
        Assertions.assertEquals("Category not found with id: 100", exception.getMessage());
    }

    @Test
    @DisplayName("Test update with existing category")
    void update_validRequest_returnResponse() {
        when(categoryRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(VALID_CATEGORY));
        when(categoryMapper.toModel(REQUEST_DTO)).thenReturn(VALID_CATEGORY);
        when(categoryRepository.save(VALID_CATEGORY)).thenReturn(VALID_CATEGORY);
        when(categoryMapper.toDto(VALID_CATEGORY)).thenReturn(VALID_RESPONSE);

        CategoryDto actual = categoryService.update(1L, REQUEST_DTO);
        Assertions.assertEquals(VALID_RESPONSE, actual);
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toModel(REQUEST_DTO);
        verify(categoryRepository).save(VALID_CATEGORY);
        verify(categoryMapper).toDto(VALID_CATEGORY);
    }

    @Test
    @DisplayName("Test deleteById with valid category ID")
    void deleteById_validCategoryId_successfullyDeleted() {
        Long validCategoryId = 1L;
        when(categoryRepository.findById(validCategoryId)).thenReturn(Optional.of(VALID_CATEGORY));
        doNothing().when(categoryRepository).delete(VALID_CATEGORY);

        Assertions.assertDoesNotThrow(() -> categoryService.deleteById(validCategoryId));
        verify(categoryRepository).delete(VALID_CATEGORY);
        verify(categoryRepository).findById(validCategoryId);
    }
}
