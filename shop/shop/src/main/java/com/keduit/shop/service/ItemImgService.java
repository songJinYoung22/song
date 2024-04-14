package com.keduit.shop.service;

import com.keduit.shop.entity.ItemImg;
import com.keduit.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile multipartFile)
        throws Exception{
        String oriImgName = multipartFile.getOriginalFilename();
        String imgName = "";
        String imgUrl  = "";

        // 파일 업로드
        // 등록여부를 확인후 서비스 업로드파일스 호출
        // 이때 경로 이름 이미지파일의 바이트배열을 파라미터로 전달
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    multipartFile.getBytes());

            imgUrl = "/images/item/" + imgName;
            //저장한 파일을 불러올 경로 설정
            // webmvcconfig 클레스에서 "/imgase/**"로 설정했으며 업로드페치 프로퍼티 경로인
            // f 샵 아래 item 폴더에 이미지를 저장하므로 상품이미지 경로는 /tem을 붙인다.
        }
        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    // 여기서부터 공부
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
            throws Exception{
        if( !itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation + "/"
                        + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(
                    itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
