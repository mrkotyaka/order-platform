package ru.mrkotyaka.orderservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrkotyaka.commonlibs.dto.item.ItemRqDto;
import ru.mrkotyaka.commonlibs.dto.item.ItemRsDto;
import ru.mrkotyaka.orderservice.domain.db.ItemMapper;
import ru.mrkotyaka.orderservice.domain.db.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemProcessor {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    @Transactional(readOnly = true)
    public List<ItemRsDto> getAllItems() {
        List<ItemRsDto> allItemRsDto = new ArrayList<>();
        var allItems = itemRepository.findAll();
        for (var item : allItems) {
            allItemRsDto.add(itemMapper.toItemRsDto(item));
        }
        return allItemRsDto;
    }

    public List<ItemRsDto> createItems(List<ItemRqDto> request) {
        List<ItemRsDto> itemsRsDto = new ArrayList<>();
        for (var itemRqDto : request) {
            var entity = itemMapper.toItemEntity(itemRqDto);
            entity.setName(itemRqDto.name());
            entity.setPrice(itemRqDto.price());
            itemRepository.save(entity);
            itemsRsDto.add(itemMapper.toItemRsDto(entity));
        }

        log.info("List of items was saved successfully");
        return itemsRsDto;
    }
}
