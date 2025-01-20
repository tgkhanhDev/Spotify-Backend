package music_service.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import music_service.dto.artistCollaborationDto.response.ArtistGeneralResponse;
import music_service.dto.authenticationDto.response.AccountResponse;
import music_service.exception.AuthenException;
import music_service.exception.ErrorCode;
import music_service.mapper.AccountMapper;
import music_service.mapper.ArtistElsMapper;
import music_service.model.Account;
import music_service.model.ArtistEls;
import music_service.repository.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ArtistServiceImpl implements ArtistService {

    AccountMapper accountMapper;
    ArtistElsRepository artistRepository;
    AccountRepository accountRepository;
    ArtistElsMapper artistElsMapper;

    public ArtistServiceImpl(AccountMapper accountMapper, ArtistElsRepository artistRepository, AccountRepository accountRepository, ArtistElsMapper artistElsMapper) {
        this.accountMapper = accountMapper;
        this.artistRepository = artistRepository;
        this.accountRepository = accountRepository;
        this.artistElsMapper = artistElsMapper;
    }

    @Override
    public List<ArtistGeneralResponse> findArtistByName(String name) {
        System.out.println("name: "+name);
        if(name == null){
            name = "";
        }
        List<ArtistEls> list = artistRepository.findByNicknameLike(name);
        return artistElsMapper.toArtistGeneralResponseList(list);
    }

    //Remove Transaction: got error java.lang.Long co.elastic.clients.elasticsearch.core.IndexResponse.seqNo()
    @Override
    public AccountResponse becomeArtist(Jwt jwtToken) {
        UUID userIdClaims = UUID.fromString(jwtToken.getClaim("userId"));

        Optional<Account> accountOptional = accountRepository.findById(userIdClaims);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            account.setSubcribe(true);
            Account accountUpdated = accountRepository.save(account);

            try {
                artistRepository.save(new ArtistEls().builder()
                        .id(jwtToken.getClaim("userId"))
                        .nickname(accountUpdated.getNickName())
                        .avatar(accountUpdated.getAvatar())
                        .build());
            }catch (Exception e) {
                System.out.println("Got Exception: " + e.getMessage());
                AccountResponse res = accountMapper.toAccountResponse(account);
                res.setSubcribe(true);
                return res;
            }

            return accountMapper.toAccountResponse(account);
        } else {
            throw new AuthenException(ErrorCode.USER_NOT_EXISTED);
        }
    }

}
