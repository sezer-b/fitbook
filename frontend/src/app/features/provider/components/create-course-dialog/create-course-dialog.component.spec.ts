import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCourseDialogComponent } from './create-course-dialog.component';

describe('CreateCourseDialog', () => {
  let component: CreateCourseDialogComponent;
  let fixture: ComponentFixture<CreateCourseDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCourseDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateCourseDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
