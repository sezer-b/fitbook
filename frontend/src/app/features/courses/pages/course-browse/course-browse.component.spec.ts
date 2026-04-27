import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseBrowseComponent } from './course-browse.component';

describe('CourseBrowse', () => {
  let component: CourseBrowseComponent;
  let fixture: ComponentFixture<CourseBrowseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseBrowseComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CourseBrowseComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
