package com.example.eventplanner.fragments.comment;

import com.example.eventplanner.dto.PageResponse;
import com.example.eventplanner.dto.comment.GetCommentDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentService {

    // Get all pending comments (admin only)
    @GET("comments/pending")
    Call<PageResponse<GetCommentDTO>> getPendingComments(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("size") int size
    );

    // Approve comment (admin only)
    @PATCH("comments/{id}/approve")
    Call<Void> approveComment(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    // Delete comment (admin only)
    @PATCH("comments/{id}/delete")
    Call<Void> deleteComment(
            @Header("Authorization") String token,
            @Path("id") Long id
    );

    // Delete own comment
    @PATCH("comments/{id}/user-delete")
    Call<Void> deleteOwnComment(
            @Header("Authorization") String token,
            @Path("id") Long id
    );



    // Get comment by id
    @GET("comments/{entityId}/{id}")
    Call<GetCommentDTO> getCommentById(
            @Header("Authorization") String token,
            @Path("entityId") Long entityId,
            @Path("id") Long id
    );



    // Can user comment?
    @GET("comments/can-comment")
    Call<Boolean> canUserComment(
            @Header("Authorization") String token,
            @Query("entityType") String entityType,
            @Query("entityId") Long entityId
    );
}
